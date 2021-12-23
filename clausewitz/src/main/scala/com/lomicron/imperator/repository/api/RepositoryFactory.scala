package com.lomicron.imperator.repository.api

trait RepositoryFactory {
  def resources: ResourceRepository
  def buildings: BuildingRepository
  def popTypes: PopTypeRepository
  def provinces: ProvinceRepository
}
