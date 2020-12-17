package com.lomicron.oikoumene.repository.inmemory

import com.lomicron.oikoumene.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.oikoumene.repository.api.gfx.GFXRepository
import com.lomicron.oikoumene.repository.api.government.{GovernmentReformRepository, GovernmentRepository, IdeaGroupRepository, TechnologyRepository}
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.modifiers.ModifierRepository
import com.lomicron.oikoumene.repository.api.politics._
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.repository.api.trade.{CenterOfTradeRepository, TradeGoodRepository, TradeNodeRepository}
import com.lomicron.oikoumene.repository.api.{GameFilesSettings, RepositoryFactory}
import com.lomicron.oikoumene.repository.fs.{CacheReader, CacheWriter, FSGFXRepository, FileResourceRepository}
import com.lomicron.oikoumene.repository.inmemory.diplomacy.{InMemoryCasusBelliRepository, InMemoryDiplomacyRepository, InMemoryWarGoalTypeRepository, InMemoryWarRepository}
import com.lomicron.oikoumene.repository.inmemory.government.{InMemoryGovernmentReformRepository, InMemoryGovernmentRepository, InMemoryIdeaGroupRepository, InMemoryTechGroupRepository, InMemoryTechnologyRepository}
import com.lomicron.oikoumene.repository.inmemory.map._
import com.lomicron.oikoumene.repository.inmemory.modifiers.InMemoryModifierRepository
import com.lomicron.oikoumene.repository.inmemory.politics._
import com.lomicron.oikoumene.repository.inmemory.trade.{InMemoryCenterOfTradeRepository, InMemoryTradeGoodRepository, InMemoryTradeNodeRepository}
import com.lomicron.oikoumene.writers.{FileWriterFactory, ModSettings, WriterFactory}

case class InMemoryRepositoryFactory(settings: GameFilesSettings)
  extends RepositoryFactory { self =>

  private val files = FileResourceRepository(settings)
  private val localisation: LocalisationRepository = InMemoryLocalisationRepository()

  private val tagRepo: TagRepository = InMemoryTagRepository()
  private val cultureGroupRepo: CultureGroupRepository = InMemoryCultureGroupRepository()
  private val cultureRepo: CultureRepository = InMemoryCultureRepository(cultureGroupRepo)
  private val religionGroupRepo: ReligionGroupRepository = InMemoryReligionGroupRepository()
  private val religionRepo: ReligionRepository = InMemoryReligionRepository(religionGroupRepo)

  private val governmentsRepo = InMemoryGovernmentRepository()
  private val governmentReformsRepo = InMemoryGovernmentReformRepository()
  private val techGroupRepo = InMemoryTechGroupRepository()
  private val technologyRepo = InMemoryTechnologyRepository(techGroupRepo)
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

  private val tradeGoodRepository: TradeGoodRepository = InMemoryTradeGoodRepository()
  private val tradeNodeRepository: TradeNodeRepository = InMemoryTradeNodeRepository()
  private val centerOfTradeRepository: CenterOfTradeRepository = InMemoryCenterOfTradeRepository()

  private val eventModifierRepository: ModifierRepository = InMemoryModifierRepository()



  override def resources: ResourceRepository = files
  override def localisations: LocalisationRepository = localisation

  override def tags: TagRepository = tagRepo
  override def cultures: CultureRepository = cultureRepo
  override def religions: ReligionRepository = religionRepo

  override def governments: GovernmentRepository = governmentsRepo
  override def governmentReforms: GovernmentReformRepository = governmentReformsRepo
  override def technology: TechnologyRepository = technologyRepo
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

  override def tradeGoods: TradeGoodRepository = tradeGoodRepository
  override def tradeNodes: TradeNodeRepository = tradeNodeRepository
  override def centersOfTrade: CenterOfTradeRepository = centerOfTradeRepository

  override def modifiers: ModifierRepository = eventModifierRepository

  override def gfx: GFXRepository = FSGFXRepository(this)

  override def modWriters(mod: String): WriterFactory =
    modWriters(Option(mod))

  private def modWriters(mod: Option[String]): WriterFactory = {
    val modSettings = ModSettings(eu4ModDir = settings.modDir, modDir = mod)
    FileWriterFactory(modSettings, files)
  }

  override def storeToCache: RepositoryFactory =
    CacheWriter(this).store

  override def loadFromCache: Option[RepositoryFactory] =
    CacheReader(this).load

}
