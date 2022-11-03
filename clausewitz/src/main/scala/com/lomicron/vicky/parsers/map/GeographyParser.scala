package com.lomicron.vicky.parsers.map

import com.lomicron.oikoumene.model.map.Adjacency
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.vicky.repository.api.{GeographicRepository, MapRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

import scala.jdk.javaapi.CollectionConverters
import scala.util.matching.Regex

object GeographyParser extends LazyLogging {

  val adjacencyPat: Regex =
    "^(?<from>\\d+);(?<to>\\d+);(?<type>[a-zA-Z]*);(?<throuh>\\d+);-?(?<data>\\d+);(?<comment>.*)".r

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
    RegionParser(files, localisation, geography.regions, evalEntityFields)
    TerrainParser(files, localisation, geography, evalEntityFields)
    ProvinceTypeParser(files, geography, evalEntityFields)
//    ProvincePositionsParser(files, geography, evalEntityFields)
//    LakeParser(files, geography, evalEntityFields)

    geography
  }

  def parseAdjacencies(files: ResourceRepository, map: MapRepository): MapRepository = {
    val as = files
      .getAdjacencies
      .map(_.lines.toList)
      .map(CollectionConverters.asScala(_).toSeq)
      .getOrElse(Seq.empty)
      .flatMap(parseAdjacency)
    map.updateAdjacencies(as)
  }

  def parseAdjacency(line: String): Option[Adjacency] =
    line match {
      case adjacencyPat(from, to, aType, through, data, comment) =>
        Some(Adjacency(from.toInt, to.toInt, aType, through.toInt, 0, 0, 0, 0, comment))
      case _ => None
    }


}
