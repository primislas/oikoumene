package com.lomicron.imperator.parsers.map

import com.lomicron.eu4.model.map.{Route, RouteTypes, TileRoute}
import com.lomicron.eu4.model.provinces.ProvinceTypes
import com.lomicron.eu4.repository.api.map.MapRepository
import com.lomicron.imperator.model.provinces.{Province, ProvinceGeography}
import com.lomicron.imperator.repository.api.{GeographicRepository, ProvinceRepository, ProvinceTypesRepository, RepositoryFactory, ResourceRepository}
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
    "^(?<provinceType>[a-zA-Z_]+)\\s*=\\s*(?<declarationType>LIST|RANGE)\\s*\\{(?<values>[\\d\\s]+)}".r

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository =
    apply(repos.provinces, repos.resources, repos.localisation, repos.geography, evalEntityFields)

  def apply
  (
    provinces: ProvinceRepository,
    files: ResourceRepository,
    localisation: LocalisationRepository,
    geography: GeographicRepository,
    evalEntityFields: Boolean
  ): GeographicRepository = {

    parseMapConfig(files, geography.provinceTypes)
    parseAdjacencies(files, geography.map)
    parseAreas(files, localisation, geography, evalEntityFields)
    parseRegions(files, localisation, geography, evalEntityFields)
    provinces
      .findAll
      .map(p => {
        val ttype = geography.provinceTypes.typeOfProvince(p.id).getOrElse(ProvinceTypes.province)
        val area = geography.areas.areaOfProvince(p.id).map(_.id)
        val region = area.flatMap(geography.regions.regionOfArea).map(_.id)
        val pg = ProvinceGeography(ttype, area, region)
        p.withGeography(pg)
      })
      .foreach(provinces.update)

    buildRoutes(geography.map, provinces)

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
        val ids = values.split(" ").filter(_.nonEmpty).flatMap(_.toIntOption).toSeq
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

  def buildRoutes(map: MapRepository, provinces: ProvinceRepository): MapRepository = {
    val trs = buildTileRoutes(map, provinces)
    val ars = adjacenciesToRoutes(map, provinces)
    map.updateRoutes(trs ++ ars)
  }

  def buildTileRoutes(map: MapRepository, provinces: ProvinceRepository): Seq[Route] =
    map
      .tileRoutes
      .flatMap(r => Seq(r, r.inverse))
      .flatMap(buildRoute(_, provinces))

  def buildRoute(r: TileRoute, provinces: ProvinceRepository): Option[Route] =
    for {
      source <- provinces.findByColor(r.source)
      target <- provinces.findByColor(r.target)
    } yield routeBetween(source, target)

  def routeBetween(source: Province, target: Province): Route = {
    val rType = routeTypeBetween(source, target)
    Route(source.id, target.id, rType)
  }

  def routeTypeBetween(source: Province, target: Province): String = (source.geography.`type`, target.geography.`type`) match {
    case (Some(sType), Some(dType)) => (sType, dType) match {
      case (ProvinceTypes.province, ProvinceTypes.province) => RouteTypes.LAND
      case (ProvinceTypes.province, ProvinceTypes.sea) => RouteTypes.BOARDING
      case (ProvinceTypes.sea, ProvinceTypes.province) => RouteTypes.LANDING
      case (ProvinceTypes.sea, ProvinceTypes.sea) => RouteTypes.SEA
      case _ => RouteTypes.IMPASSABLE
    }
    case _ => RouteTypes.IMPASSABLE
  }

  def adjacenciesToRoutes(map: MapRepository, provinces: ProvinceRepository): Seq[Route] = {
    val rType = RouteTypes.CROSSING
    val rs = map
      .adjacencies
      .flatMap(a => for {
        source <- provinces.find(a.from)
        target <- provinces.find(a.to)
      } yield Seq(Route(source.id, target.id, rType), Route(target.id, source.id, rType)))

    rs.flatten
  }



}
