import com.google.inject.AbstractModule
import com.lomicron.eu4.repository.api.RepositoryFactory
import net.codingwell.scalaguice.ScalaModule
import repository.inmemory.InMemoryRepos

class OnStartupModule extends AbstractModule with ScalaModule {

  override def configure(): Unit = {
    bind(classOf[OnStartup]).asEagerSingleton()
    bind[RepositoryFactory].to[InMemoryRepos]
  }

}

object OnStartupModule {
  def apply(): OnStartupModule = new OnStartupModule()
}
