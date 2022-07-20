package com.lomicron.eu4.parsers.provinces

import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.map.{GeographicRepository, MapRepository}
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.map.AdjacencyParser
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.typesafe.scalalogging.LazyLogging

object GeographyParser extends LazyLogging with AdjacencyParser {

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
    LakeParser(files, geography, evalEntityFields)

    geography
  }

  def parseAdjacencies(files: ResourceRepository, map: MapRepository): MapRepository = {
    val as = files
      .getAdjacencies
      .map(parseAdjacencies)
      .getOrElse(Seq.empty)
    map.updateAdjacencies(as)
  }


}