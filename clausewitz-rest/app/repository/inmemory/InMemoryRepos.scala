package repository.inmemory

import com.lomicron.oikoumene.repository.api.diplomacy.{CasusBelliRepository, DiplomacyRepository, WarGoalTypeRepository, WarHistoryRepository}
import com.lomicron.oikoumene.repository.api.government.IdeaGroupRepository
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.politics.{CultureRepository, ReligionRepository, TagRepository}
import com.lomicron.oikoumene.repository.api.{LocalisationRepository, RepositoryFactory, ResourceRepository}

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
}

object InMemoryReposSingleton {
  private var repos: RepositoryFactory = _

  def getRepos: RepositoryFactory = this.repos

  def setRepos(repos: RepositoryFactory): Unit = {
    this.repos = repos
  }

}
