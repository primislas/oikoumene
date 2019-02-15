package com.lomicron.oikoumene.repository.api.map

trait GeographicRepository {

  def areas: AreaRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository
  def continent: ContinentRepository
  def colonies: ColonialRegionRepository

  def terrain: TerrainRepository
  def climate: ClimateRepository

}
