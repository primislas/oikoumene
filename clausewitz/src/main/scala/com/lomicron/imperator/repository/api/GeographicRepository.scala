package com.lomicron.imperator.repository.api

import com.lomicron.eu4.repository.api.map.MapRepository
import com.lomicron.oikoumene.repository.api.map.{AreaRepository, RegionRepository}

trait GeographicRepository {

  def provinceTypes: ProvinceTypesRepository

  def map: MapRepository

  def areas: AreaRepository

  def regions: RegionRepository

}
