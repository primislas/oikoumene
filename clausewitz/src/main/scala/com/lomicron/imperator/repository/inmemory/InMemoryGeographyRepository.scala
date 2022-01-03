package com.lomicron.imperator.repository.inmemory

import com.lomicron.eu4.repository.api.map.MapRepository
import com.lomicron.eu4.repository.inmemory.map.InMemoryMapRepository
import com.lomicron.imperator.repository.api.{GeographicRepository, ProvinceTypesRepository}
import com.lomicron.oikoumene.repository.api.map.{AreaRepository, RegionRepository}
import com.lomicron.oikoumene.repository.inmemory.map.{InMemoryAreaRepository, InMemoryRegionRepository}

object InMemoryGeographyRepository extends GeographicRepository {

  private val _pTypes = InMemoryProvinceTypeRepository
  private val _map = InMemoryMapRepository()
  private val _areas = InMemoryAreaRepository
  private val _regions = InMemoryRegionRepository

  override def provinceTypes: ProvinceTypesRepository = _pTypes

  override def map: MapRepository = _map

  override def areas: AreaRepository = _areas

  override def regions: RegionRepository = _regions

}
