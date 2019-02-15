package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.map._

trait RepositoryFactory {

  def resources: ResourceRepository
  def localisations: LocalisationRepository


  def tags: TagRepository
  def provinces: ProvinceRepository


  def geography: GeographicRepository
  def regions: RegionRepository
  def superregions: SuperRegionRepository


}
