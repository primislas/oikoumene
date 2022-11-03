package com.lomicron.vicky.repository.api

import com.lomicron.oikoumene.model.provinces.ProvinceTypes
import com.lomicron.oikoumene.repository.api.map.{ClimateRepository, ContinentRepository, RegionRepository, TerrainRepository}

trait GeographicRepository { self =>

  private var pTypes: Option[ProvinceTypes] = None

  def regions: RegionRepository
  def continent: ContinentRepository

  def provinceTypes(pt: ProvinceTypes): GeographicRepository = {
    pTypes = Option(pt)
    self
  }
  def provinceTypes: Option[ProvinceTypes] = pTypes
  def terrain: TerrainRepository
  def climate: ClimateRepository

  def map: MapRepository

}
