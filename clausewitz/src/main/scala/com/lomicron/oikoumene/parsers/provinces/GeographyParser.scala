package com.lomicron.oikoumene.parsers.provinces

import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

object GeographyParser extends LazyLogging {

  def apply(repos: RepositoryFactory): GeographicRepository =
    apply(repos.resources, repos.localisations, repos.geography)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository): GeographicRepository = {

    AreaParser(files, localisation, geography.areas)
    RegionParser(files, localisation, geography.regions)
    SuperRegionParser(files, localisation, geography.superregions)
    ContinentParser(files, localisation, geography.continent)
    ColonialRegionParser(files, localisation, geography.colonies)
    TerrainParser(files, localisation, geography.terrain)
    ClimateParser(files, localisation, geography.climate)

    geography
  }


}
