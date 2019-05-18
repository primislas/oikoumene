import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import repository.inmemory.InMemoryReposSingleton

class OnStartup() {
  val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
  val modDir = ""
  val repos = InMemoryRepositoryFactory(gameDir, modDir)
  Oikoumene.parseConfigs(repos)
  InMemoryReposSingleton.setRepos(repos)
}
