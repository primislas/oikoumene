package com.lomicron.imperator.parsers.map

import com.lomicron.eu4.model.provinces.ProvinceTypes
import com.lomicron.eu4.repository.api.map.MapRepository
import com.lomicron.imperator.repository.api.{GeographicRepository, ProvinceTypesRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.oikoumene.model.provinces.{Area, Region}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, EntityParser}
import com.lomicron.oikoumene.parsers.map.AdjacencyParser
import com.lomicron.oikoumene.repository.api.map.{AreaRepository, RegionRepository}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.typesafe.scalalogging.LazyLogging

import scala.util.matching.Regex

object GeographyParser extends AdjacencyParser with LazyLogging {

  val provinceTypePat: Regex =
    "^(?<province_type>[a-zA-Z_]+)\\s*=\\s*(?<declaration_type>LIST|RANGE)\\s*\\{(?<values>[\\d\\s]+)}".r
  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository =
    apply(repos.resources, repos.localisation, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean
  ): GeographicRepository = {

    parseMapConfig(files, geography.provinceTypes)
    parseAdjacencies(files, geography.map)
    parseAreas(files, localisation, geography, evalEntityFields)
    parseRegions(files, localisation, geography, evalEntityFields)

    geography
  }

  def parseMapConfig
  (files: ResourceRepository,
   pTypes: ProvinceTypesRepository
  ): ProvinceTypesRepository = {

    files
      .getProvinceTypes
      .flatMap(_.content)
      .map(_.linesIterator.toSeq)
      .getOrElse(Seq.empty)
      .map(parseProvinceType(_, pTypes))

    pTypes
  }

  def parseProvinceType
  (line: String,
   pTypes: ProvinceTypesRepository
  ): ProvinceTypesRepository = {

    line match {
      case provinceTypePat(pType, dType, values) =>
        val `type` = mapProvinceType(pType)
        val ids = values.split(" ").filter(_.nonEmpty).flatMap(_.toIntOption)
        dType.toLowerCase match {
          case "range" =>
            (ids.head to ids.last).foreach(pTypes.add(_, `type`))
          case "list" =>
            pTypes.add(ids, `type`)
        }
      case _ =>
    }

    pTypes
  }

  def mapProvinceType(pType: String): String = pType match {
    case "impassable_terrain" => ProvinceTypes.impassable
    case "sea_zones" => ProvinceTypes.sea
    case "lakes" => ProvinceTypes.lake
    case "river_provinces" => ProvinceTypes.river
    case _ => pType
  }

  def parseAreas(files: ResourceRepository,
                 localisation: LocalisationRepository,
                 geography: GeographicRepository,
                 evalEntityFields: Boolean): AreaRepository =
//    parseFileFieldsAsEntities(files.getAreas, geography.areas, Area.fromJson, localisation, evalEntityFields, "Area")
  EntityParser.parseEntities[String, Area, AreaRepository](
    files.getAreas.toSeq,
    geography.areas,
    ClausewitzParser.parseFileFieldsAsEntities,
    Area.fromJson,
    localisation,
    evalEntityFields,
    "Area"
  )

  def parseRegions(files: ResourceRepository,
                   localisation: LocalisationRepository,
                   geography: GeographicRepository,
                   evalEntityFields: Boolean): RegionRepository =
//    parseFileFieldsAsEntities(files.getRegions, geography.regions, Region.fromJson, localisation, evalEntityFields, "Region")
    EntityParser.parseEntities[String, Region, RegionRepository](
      files.getRegions.toSeq,
      geography.regions,
      ClausewitzParser.parseFileFieldsAsEntities,
      Region.fromJson,
      localisation,
      evalEntityFields,
      "Region"
    )
  def parseAdjacencies(files: ResourceRepository, map: MapRepository): MapRepository = {
    val as = files
      .getAdjacencies
      .map(_.linesIterator.toList)
      .getOrElse(Seq.empty)
      .flatMap(parseAdjacency)
    map.updateAdjacencies(as)
  }

}
