package com.lomicron.eu4.repository.inmemory.map

import com.lomicron.eu4.repository.api.map._
import com.lomicron.oikoumene.repository.api.map.{AreaRepository, RegionRepository}
import com.lomicron.oikoumene.repository.inmemory.map.{InMemoryAreaRepository, InMemoryRegionRepository}

object InMemoryGeographyRepository
extends GeographicRepository {

  private val mapRepo = InMemoryMapRepository()

  override def areas: AreaRepository = InMemoryAreaRepository

  override def regions: RegionRepository = InMemoryRegionRepository

  override def superregions: SuperRegionRepository = InMemorySuperRegionRepository

  override def continent: ContinentRepository = InMemoryContinentRepository

  override def colonies: ColonialRegionRepository = InMemoryColonialRegionRepository

  override def terrain: TerrainRepository = InMemoryTerrainRepository

  override def climate: ClimateRepository = InMemoryClimateRepository

  override def map: MapRepository = mapRepo

}
