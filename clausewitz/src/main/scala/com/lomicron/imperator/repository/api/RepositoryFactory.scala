package com.lomicron.imperator.repository.api

import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository

trait RepositoryFactory {
  def resources: ResourceRepository
  def localisation: LocalisationRepository
  def geography: GeographicRepository

  def buildings: BuildingRepository
  def popTypes: PopTypeRepository
  def provinces: ProvinceRepository

  def tags: TagRepository

}
