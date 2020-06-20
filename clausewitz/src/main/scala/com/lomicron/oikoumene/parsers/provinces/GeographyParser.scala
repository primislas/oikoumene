package com.lomicron.oikoumene.parsers.provinces

import com.lomicron.oikoumene.model.map.Adjacency
import com.lomicron.oikoumene.repository.api.map.{GeographicRepository, MapRepository}
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

import scala.util.matching.Regex

object GeographyParser extends LazyLogging {

  val adjacencyPat: Regex =
    "^(?<from>\\d+);(?<to>\\d+);(?<type>[a-zA-Z]*);(?<throuh>\\d+);(?<startX>\\d+);(?<startY>\\d+);(?<stopX>\\d+);(?<stopY>\\d+);(?<comment>.*)".r

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository =
    apply(repos.resources, repos.localisations, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    parseAdjacencies(files, geography.map)
    AreaParser(files, localisation, geography.areas, evalEntityFields)
    RegionParser(files, localisation, geography.regions, evalEntityFields)
    SuperRegionParser(files, localisation, geography.superregions, evalEntityFields)
    ContinentParser(files, localisation, geography.continent, evalEntityFields)
    ColonialRegionParser(files, localisation, geography.colonies, evalEntityFields)
    TerrainParser(files, localisation, geography, evalEntityFields)
    ClimateParser(files, localisation, geography.climate, evalEntityFields)
    ProvinceTypeParser(files, geography, evalEntityFields)
    ProvincePositionsParser(files, geography, evalEntityFields)

    geography
  }

  def parseAdjacencies(files: ResourceRepository, map: MapRepository): MapRepository = {
    val as = files
      .getAdjacencies
      .map(_.lines.toSeq)
      .getOrElse(Seq.empty)
      .flatMap(parseAdjacency)
    map.updateAdjacencies(as)
  }

  def parseAdjacency(line: String): Option[Adjacency] =
    line match {
      case adjacencyPat(from, to, aType, through, startX, startY, stopX, stopY, comment) =>
        Some(Adjacency(from.toInt, to.toInt, aType, through.toInt, startX.toInt, startY.toInt, stopX.toInt, stopY.toInt, comment))
      case _ => None
    }


}
