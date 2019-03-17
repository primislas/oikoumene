package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics.{CultureRepository, ReligionRepository, TagRepository}

trait RepositoryFactory {

  def resources: ResourceRepository
  def localisations: LocalisationRepository


  def tags: TagRepository
  def cultures: CultureRepository
  def religions: ReligionRepository

  def provinces: ProvinceRepository
  def buildings: BuildingRepository
  def geography: GeographicRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository

}
