package com.lomicron.oikoumene.engine

import java.nio.file.Paths

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode, TextNode}
import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.parsers._
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{objectNode, patchFieldValue}
import com.lomicron.utils.parsing.scopes.ObjectScope
import com.lomicron.utils.parsing.serialization.BaseDeserializer
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.TreeMap

object Oikoumene extends LazyLogging {

  def main(args: Array[String]) {
    logger.info("Starting the known world...")

    val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
    val modDir = ""

    val repos = InMemoryRepositoryFactory(gameDir, modDir)
    val files = repos.resources
    val localisation = repos.localisations

    val tags = loadTags(files, localisation, repos.tags)
    val buildings = BuildingParser(files, localisation, repos.buildings)
    val provinces = loadProvinces(files, localisation, repos.provinces, repos.buildings)
    val geograpy = loadGeographicData(files, localisation, repos.geography)
    val religions = ReligionParser(files, localisation, repos.religions)
    val cultures = CultureParser(files, localisation, repos.cultures)

    logger.info("Bye")
  }

  private def loadMap(): Seq[Tile] = {
    logger.info("Loading provinces...")
    val rootDir = System.getProperty("user.dir")
    val relativeMapDir = "./clausewitz/resources/"
    val mapDir = Paths.get(rootDir, relativeMapDir)
    val map = MapLoader.loadMap(mapDir).get
    val tiles = map._1
    val routes = map._2
    logger.info("Loaded " + tiles.size + " tiles, :" + tiles)
    val l: List[Int] = Nil
    tiles
  }

  val idKey = "id"

  private def loadTags
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   tags: TagRepository
  ): TagRepository = {

    val filesByTags = files
      .getCountryTags
      .map(contentsByFile => ClausewitzParser.parse(contentsByFile._2)._1)
      .flatMap(obj => obj.fields.toStream.map(e => (e.getKey, e.getValue.asText)))
      .map(kv => (kv._1, s"common/${kv._2}"))
      .foldLeft(TreeMap[String, String]())(_ + _)
    val countries = files.getCountries(filesByTags)
    val histories = files.getCountryHistory
    val names = localisation.fetchTags
    val parsedTags = TagParser(filesByTags, countries, histories, names)
      .mapKVtoValue((id, tag) => tag.set(idKey, TextNode.valueOf(id)).asInstanceOf[ObjectNode])
    tags.create(parsedTags.values.to[Seq])
    tags
  }

  private def loadProvinces
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   provinces: ProvinceRepository,
   buildings: BuildingRepository): ProvinceRepository = {

    val definitions = files.getProvinceDefinitions
    val provinceTypes = files.getProvinceTypes // default.map
    val provincePositions = files.getProvincePositions
    val history = files.getProvinceHistory

    ProvinceParser(
      definitions,
      provinceTypes,
      provincePositions,
      history,
      localisation,
      buildings,
      provinces)
  }

  private def loadGeographicData
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository) = {

    loadAreas(files, localisation, geography.areas)
    loadRegions(files, localisation, geography.regions)
    loadSuperRegions(files, localisation, geography.superregions)
    loadContinents(files, localisation, geography.continent)
    loadColonialRegions(files, localisation, geography.colonies)
    loadTerrain(files, localisation, geography.terrain)
    loadClimate(files, localisation, geography.climate)

    geography
  }

  val provinceIdsKey = "province_ids"

  private def loadAreas
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   areaRepo: AreaRepository) = {
    files
      .getAreas
      .map(ClausewitzParser.parse(_, BaseDeserializer))
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing areas: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue)
      .toMap
      .filterValues(_.size() > 0)
      .mapValues {
        case o@(_: ArrayNode) =>
          val area = objectNode
          area.set(provinceIdsKey, o)
          area
        case area: ObjectNode =>
          val ids = area.remove(ObjectScope.arrayKey)
          Option(ids).foreach(area.set(provinceIdsKey, _))
          area
        case default =>
          logger.warn("Unexpected area definition, omitting: {}", default.toString)
          objectNode
      }
      .mapKVtoValue((id, area) => patchFieldValue(area, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .mapValues(ClausewitzParser.unwrapColor)
      .values
      .foreach(areaRepo.create)

    areaRepo
  }

  private def loadRegions
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   regions: RegionRepository) = {

    files
      .getRegions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing regions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected region ObjectNode but encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, region) => patchFieldValue(region, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .foreach(regions.create)

    regions
  }

  val regionIdsKey = "region_ids"

  private def loadSuperRegions
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   superregions: SuperRegionRepository) = {

    files
      .getSuperregions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing superregions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ArrayNode])
          logger.warn(s"Expected super-region ArrayNodes but encountered ${n.toString}")
        n.isInstanceOf[ArrayNode]
      })
      .mapValues(_.asInstanceOf[ArrayNode])
      .mapValues(objectNode.set(regionIdsKey, _).asInstanceOf[ObjectNode])
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .foreach(superregions.create)

    superregions
  }

  private def loadContinents
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   continents: ContinentRepository) = {

    files
      .getContinents
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing superregions: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ArrayNode])
          logger.warn(s"Expected super-region ArrayNodes but encountered ${n.toString}")
        n.isInstanceOf[ArrayNode]
      })
      .mapValues(_.asInstanceOf[ArrayNode])
      .mapValues(objectNode.set(provinceIdsKey, _).asInstanceOf[ObjectNode])
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .foreach(continents.create)

    ConfigField.printCaseClass("Continent", continents.findAll)

    continents
  }

  private def loadColonialRegions(files: ResourceRepository,
                                  localisation: LocalisationRepository,
                                  colonialRegions: ColonialRegionRepository) = {
    files
      .getColonialRegions
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing terrain: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterValues(n => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected colonial region ObjectNodes but encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapValues(JsonMapper.renameField(_, "provinces", provinceIdsKey))
      .mapKVtoValue((id, sRegion) => patchFieldValue(sRegion, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .map(ClausewitzParser.unwrapColor)
      .foreach(colonialRegions.create)

    ConfigField.printCaseClass("ColonialRegion", colonialRegions.findAll)

    colonialRegions
  }


  private val terrainCategoriesKey = "categories"
  private val terrainProvincesKey = "terrain_override"

  private def loadTerrain
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   terrainRepo: TerrainRepository) = {

    val terrainById = files.getTerrain.map(parseTerrainConf).getOrElse(Map.empty)
    val preppedTerrainById = processTerrain(terrainById)
    val withLocalisation = preppedTerrainById.mapKVtoValue(localisation.findAndSetAsLocName)
    withLocalisation.values.foreach(terrainRepo.create)

    terrainRepo
  }

  /**
    * Extracts terrain definitions from config file
    *
    * @param conf terrain.txt as string
    * @return terrains mapped to their ids
    */
  private def parseTerrainConf(conf: String) =
    Option(conf)
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing terrain: ${o._2}")
        o._1
      })
      .filter(o => if (!o.has(terrainCategoriesKey)) {
        logger.warn("Found no terrain categories")
        false
      } else if (!o.get(terrainCategoriesKey).isObject) {
        logger.warn(s"Expected terrain categories to be declared as JSON object, instead encountered: ${o.get(terrainCategoriesKey).toString}")
        false
      } else true)
      .map(_.get(terrainCategoriesKey).asInstanceOf[ObjectNode])
      .map(_.fields().toStream)
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap

  private def processTerrain(terrainById: Map[String, JsonNode]) =
    terrainById
      .filterValues(n => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected terrain ObjectNode but encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, terrain) => patchFieldValue(terrain, idKey, TextNode.valueOf(id)))
      .mapValues(JsonMapper.renameField(_, terrainProvincesKey, provinceIdsKey))
      .mapValues(ClausewitzParser.unwrapColor)

  private def loadClimate
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   climates: ClimateRepository) = {

    files
      .getClimate
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing climate: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(Stream.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((id, n) => {
        if (!n.isInstanceOf[ArrayNode])
          logger.warn(s"Expected climate ArrayNode but at '$id' encountered ${n.toString}")
        n.isInstanceOf[ArrayNode]
      })
      .mapValues(_.asInstanceOf[ArrayNode])
      .mapValues(patchFieldValue(objectNode, provinceIdsKey, _))
      .mapKVtoValue((id, region) => patchFieldValue(region, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values
      .foreach(climates.create)

    climates
  }


}