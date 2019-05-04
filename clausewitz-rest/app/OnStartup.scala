import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import repository.inmemory.{InMemoryRepos, InMemoryReposSingleton}

class OnStartup() {
  val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
  val modDir = ""
  val repos = InMemoryRepositoryFactory(gameDir, modDir)
  Oikoumene.populateRepos(repos)
  InMemoryReposSingleton.setRepos(repos)
}