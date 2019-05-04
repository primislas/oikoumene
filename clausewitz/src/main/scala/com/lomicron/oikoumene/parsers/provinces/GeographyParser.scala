package com.lomicron.oikoumene.parsers.provinces

import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

object GeographyParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository =
    apply(repos.resources, repos.localisations, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    AreaParser(files, localisation, geography.areas, evalEntityFields)
    RegionParser(files, localisation, geography.regions, evalEntityFields)
    SuperRegionParser(files, localisation, geography.superregions, evalEntityFields)
    ContinentParser(files, localisation, geography.continent, evalEntityFields)
    ColonialRegionParser(files, localisation, geography.colonies, evalEntityFields)
    TerrainParser(files, localisation, geography.terrain, evalEntityFields)
    ClimateParser(files, localisation, geography.climate, evalEntityFields)
    ProvinceTypeParser(files, geography, evalEntityFields)

    // TODO province positions?
//    val provincePositions = files.getProvincePositions

    geography
  }


}
