package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.ProvinceTypes

trait GeographicRepository { self =>

  private var pTypes: Option[ProvinceTypes] = None

  def areas: AreaRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository
  def continent: ContinentRepository
  def colonies: ColonialRegionRepository

  def provinceTypes(pt: ProvinceTypes): GeographicRepository = {
    pTypes = Option(pt)
    self
  }
  def provinceTypes: Option[ProvinceTypes] = pTypes
  def terrain: TerrainRepository
  def climate: ClimateRepository

}
