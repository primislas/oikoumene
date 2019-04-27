package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics._

trait RepositoryFactory {

  def resources: ResourceRepository
  def localisations: LocalisationRepository

  def tags: TagRepository
  def cultures: CultureRepository
  def religions: ReligionRepository
  def diplomacy: DiplomacyRepository
  def warHistory: WarHistoryRepository

  def provinces: ProvinceRepository
  def buildings: BuildingRepository
  def geography: GeographicRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository

}
