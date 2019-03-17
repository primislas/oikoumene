package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics._
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.repository.inmemory.map._
import com.lomicron.oikoumene.repository.inmemory.politics._

case class InMemoryRepositoryFactory(gameDir: String, modDir: String) extends RepositoryFactory {

  private val files = FileResourceRepository(gameDir, modDir)
  private val localisation: LocalisationRepository = InMemoryLocalisationRepository(files)

  private val tagRepo: TagRepository = InMemoryTagRepository()
  private val cultureGroupRepo: CultureGroupRepository = InMemoryCultureGroupRepository()
  private val cultureRepo: CultureRepository = InMemoryCultureRepository(cultureGroupRepo)
  private val religionGroupRepo: ReligionGroupRepository = InMemoryReligionGroupRepository()
  private val religionRepo: ReligionRepository = InMemoryReligionRepository(religionGroupRepo)

  private val provinceRepo: ProvinceRepository = InMemoryProvinceRepository()
  private val buildingRepo: BuildingRepository = InMemoryBuildingRepository
  private val geographyRepo: GeographicRepository = InMemoryGeographyRepository
  private val regionRepo: RegionRepository = InMemoryRegionRepository
  private val superregionRepo: SuperRegionRepository = InMemorySuperRegionRepository


  override def resources: ResourceRepository = files

  override def localisations: LocalisationRepository = localisation

  override def tags: TagRepository = tagRepo

  override def cultures: CultureRepository = cultureRepo

  override def religions: ReligionRepository = religionRepo

  override def provinces: ProvinceRepository = provinceRepo

  override def buildings: BuildingRepository = buildingRepo

  override def geography: GeographicRepository = geographyRepo

  override def regions: RegionRepository = regionRepo

  override def superregions: SuperRegionRepository = superregionRepo

}
