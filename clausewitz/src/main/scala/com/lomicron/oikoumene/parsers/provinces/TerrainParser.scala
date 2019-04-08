package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.repository.api.map.TerrainRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.patchFieldValue
import com.typesafe.scalalogging.LazyLogging

object TerrainParser extends LazyLogging {

  private val terrainCategoriesKey = "categories"
  private val terrainProvincesKey = "terrain_override"

  def apply(repos: RepositoryFactory): TerrainRepository =
    apply(repos.resources, repos.localisations, repos.geography.terrain)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   terrainRepo: TerrainRepository): TerrainRepository = {

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

}
