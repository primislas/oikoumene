package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.oikoumene.repository.api.government.IdeaGroupRepository
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics._
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.repository.inmemory.diplomacy.{InMemoryCasusBelliRepository, InMemoryDiplomacyRepository, InMemoryWarGoalTypeRepository, InMemoryWarRepository}
import com.lomicron.oikoumene.repository.inmemory.government.InMemoryIdeaGroupRepository
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

  private val ideasRepo = InMemoryIdeaGroupRepository()

  private val diplomacyRepo: DiplomacyRepository = InMemoryDiplomacyRepository()
  private val warHistoryRepo: WarHistoryRepository = InMemoryWarRepository()
  private val warGoalTypesRepo: WarGoalTypeRepository = InMemoryWarGoalTypeRepository()
  private val cbTypesRepo: CasusBelliRepository = InMemoryCasusBelliRepository()

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



  override def ideas: IdeaGroupRepository = ideasRepo



  override def provinces: ProvinceRepository = provinceRepo

  override def buildings: BuildingRepository = buildingRepo

  override def geography: GeographicRepository = geographyRepo

  override def regions: RegionRepository = regionRepo

  override def superregions: SuperRegionRepository = superregionRepo



  override def diplomacy: DiplomacyRepository = diplomacyRepo

  override def warHistory: WarHistoryRepository = warHistoryRepo

  override def casusBelli: CasusBelliRepository = cbTypesRepo

  override def warGoalTypes: WarGoalTypeRepository = warGoalTypesRepo

}
