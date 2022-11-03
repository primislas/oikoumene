package com.lomicron.vicky.repository.inmemory

import com.lomicron.oikoumene.repository.api.map.{ClimateRepository, ContinentRepository, RegionRepository, TerrainRepository}
import com.lomicron.oikoumene.repository.inmemory.map.{InMemoryClimateRepository, InMemoryContinentRepository, InMemoryRegionRepository, InMemoryTerrainRepository}
import com.lomicron.vicky.repository.api.{GeographicRepository, MapRepository}

object InMemoryGeographyRepository
extends GeographicRepository {

  private val mapRepo = InMemoryMapRepository()

  override def regions: RegionRepository = InMemoryRegionRepository

  override def continent: ContinentRepository = InMemoryContinentRepository

  override def terrain: TerrainRepository = InMemoryTerrainRepository

  override def climate: ClimateRepository = InMemoryClimateRepository

  override def map: MapRepository = mapRepo

}
