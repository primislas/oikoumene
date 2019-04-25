package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics.{CultureRepository, DiplomacyRepository, ReligionRepository, TagRepository}

trait RepositoryFactory {

  def resources: ResourceRepository
  def localisations: LocalisationRepository

  def tags: TagRepository
  def cultures: CultureRepository
  def religions: ReligionRepository
  def diplomacy: DiplomacyRepository

  def provinces: ProvinceRepository
  def buildings: BuildingRepository
  def geography: GeographicRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository

}
