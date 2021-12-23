import com.lomicron.eu4.engine.Oikoumene
import com.lomicron.eu4.repository.api.GameFilesSettings
import com.lomicron.eu4.repository.inmemory.InMemoryRepositoryFactory
import repository.inmemory.InMemoryReposSingleton

class OnStartup() {
  val gameDir = "D:/Steam/steamapps/common/Europa Universalis IV"
  val cacheDir = "C:\\Users\\konst\\Documents\\Paradox Interactive\\Europa Universalis IV\\mod\\map_rendering\\base_game_cache"
  val settings: GameFilesSettings = GameFilesSettings(Some(gameDir), cacheDir = Some(cacheDir))
  val repos: InMemoryRepositoryFactory = InMemoryRepositoryFactory(settings)
  Oikoumene.loadConfigs(repos)
  InMemoryReposSingleton.setRepos(repos)
}
