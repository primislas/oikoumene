package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.repository.api.map._

object InMemoryGeographyRepository
extends GeographicRepository {

  override def areas: AreaRepository = InMemoryAreaRepository

  override def regions: RegionRepository = InMemoryRegionRepository

  override def superregions: SuperRegionRepository = InMemorySuperRegionRepository

  override def continent: ContinentRepository = ???

  override def colonies: ColonialRegionRepository = ???

  override def terrain: TerrainRepository = InMemoryTerrainRepository

  override def climate: ClimateRepository = InMemoryClimateRepository
}
