package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.repository.api.{BuildingRepository, PopTypeRepository, ProvinceRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.imperator.repository.fs.FileResourceRepository
import com.lomicron.eu4.repository.api.GameFilesSettings

case class InMemoryRepositoryFactory(settings: GameFilesSettings) extends RepositoryFactory {

  override def resources: ResourceRepository = FileResourceRepository(settings)

  override def buildings: BuildingRepository = InMemoryBuildingRepository
  override def popTypes: PopTypeRepository = InMemoryPopTypeTypeRepository
  override def provinces: ProvinceRepository = InMemoryProvinceRepository

}
