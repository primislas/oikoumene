package repository.inmemory

import com.lomicron.eu4.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.eu4.repository.api.gfx.GFXRepository
import com.lomicron.eu4.repository.api.government._
import com.lomicron.eu4.repository.api.map._
import com.lomicron.eu4.repository.api.modifiers.ModifierRepository
import com.lomicron.eu4.repository.api.politics.{CultureRepository, ReligionRepository, RulerPersonalityRepository, TagRepository}
import com.lomicron.eu4.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.eu4.repository.api.trade.{CenterOfTradeRepository, TradeGoodRepository, TradeNodeRepository}
import com.lomicron.eu4.repository.api.{GameFilesSettings, RepositoryFactory}
import com.lomicron.eu4.repository.fs.FileResourceRepository
import com.lomicron.eu4.writers.{FileWriterFactory, ModSettings, WriterFactory}

case class InMemoryRepos() extends RepositoryFactory {
  val repos: RepositoryFactory = InMemoryReposSingleton.getRepos

  override def resources: ResourceRepository = repos.resources

  override def localisations: LocalisationRepository = repos.localisations

  override def tags: TagRepository = repos.tags

  override def cultures: CultureRepository = repos.cultures

  override def religions: ReligionRepository = repos.religions

  override def ideas: IdeaGroupRepository = repos.ideas

  override def diplomacy: DiplomacyRepository = repos.diplomacy

  override def warHistory: WarHistoryRepository = repos.warHistory

  override def casusBelli: CasusBelliRepository = repos.casusBelli

  override def warGoalTypes: WarGoalTypeRepository = repos.warGoalTypes

  override def provinces: ProvinceRepository = repos.provinces

  override def buildings: BuildingRepository = repos.buildings

  override def geography: GeographicRepository = repos.geography

  override def regions: RegionRepository = repos.regions

  override def superregions: SuperRegionRepository = repos.superregions

  override def tradeGoods: TradeGoodRepository = repos.tradeGoods

  override def tradeNodes: TradeNodeRepository = repos.tradeNodes

  override def gfx: GFXRepository = repos.gfx

  override def modWriters(mod: String): WriterFactory = {
    val fileRes = resources.asInstanceOf[FileResourceRepository]
    val settings = ModSettings(eu4ModDir = fileRes.settings.modDir, modDir = Option(mod))
    FileWriterFactory(settings, fileRes)
  }

  override def settings: GameFilesSettings = repos.settings

  override def storeToCache: RepositoryFactory = repos.storeToCache

  override def loadFromCache: Option[RepositoryFactory] = repos.loadFromCache

  override def rulerPersonalities: RulerPersonalityRepository = repos.rulerPersonalities

  override def governments: GovernmentRepository = repos.governments

  override def governmentReforms: GovernmentReformRepository = repos.governmentReforms

  override def technology: TechnologyRepository = repos.technology

  override def policies: PolicyRepository = repos.policies

  override def stateEdicts: StateEdictRepository = repos.stateEdicts

  override def centersOfTrade: CenterOfTradeRepository = repos.centersOfTrade

  override def modifiers: ModifierRepository = repos.modifiers
}

object InMemoryReposSingleton {
  private var repos: RepositoryFactory = _

  def getRepos: RepositoryFactory = this.repos

  def setRepos(repos: RepositoryFactory): Unit = {
    this.repos = repos
  }

}
