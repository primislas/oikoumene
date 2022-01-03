package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.repository.api.{BuildingRepository, GeographicRepository, PopTypeRepository, ProvinceRepository, RepositoryFactory, ResourceRepository, TagRepository}
import com.lomicron.imperator.repository.fs.FileResourceRepository
import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryLocalisationRepository

case class InMemoryRepositoryFactory(settings: GameFilesSettings) extends RepositoryFactory {
  private val _resources = FileResourceRepository(settings)
  private val _localisation = InMemoryLocalisationRepository()

  override def resources: ResourceRepository = _resources
  override def localisation: LocalisationRepository = _localisation
  override def buildings: BuildingRepository = InMemoryBuildingRepository
  override def popTypes: PopTypeRepository = InMemoryPopTypeTypeRepository
  override def provinces: ProvinceRepository = InMemoryProvinceRepository
  override def geography: GeographicRepository = InMemoryGeographyRepository
  override def tags: TagRepository = InMemoryTagRepository

}
