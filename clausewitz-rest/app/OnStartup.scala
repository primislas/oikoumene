import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.repository.api.GameFilesSettings
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import repository.inmemory.InMemoryReposSingleton

class OnStartup() {
  val gameDir = "D:/Steam/steamapps/common/Europa Universalis IV"
  val settings: GameFilesSettings = GameFilesSettings(Some(gameDir))
  val repos: InMemoryRepositoryFactory = InMemoryRepositoryFactory(settings)
  Oikoumene.loadConfigs(repos)
  InMemoryReposSingleton.setRepos(repos)
}
