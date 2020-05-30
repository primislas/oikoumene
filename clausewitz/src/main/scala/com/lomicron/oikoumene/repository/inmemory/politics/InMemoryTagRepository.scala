package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.repository.api.politics.{TagRepository, TagSearchConf}
import com.lomicron.oikoumene.repository.api.{SearchConf, SearchResult}
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository
import com.lomicron.oikoumene.service.NamingService

case class InMemoryTagRepository()
  extends InMemoryEntityRepository[Tag]
  with TagRepository {

  override def setId(entity: Tag, id: String): Tag = entity.copy(id = id)

  override def search(req: SearchConf): SearchResult[Tag] = {
    req match {
      case conf: TagSearchConf => search(conf)
      case _ => super.search(req)
    }
  }

  def search(req: TagSearchConf): SearchResult[Tag] = {
    val withoutName = findAll
      .filter(p => searchArgMatches(req.primaryCulture, p.state.primaryCulture))
      .filter(p => searchArgMatches(req.religion, p.state.religion))
      .filter(p => req.id.forall(reqId => p.id.contains(reqId.toUpperCase)))

    val allMatching = req.name.map(n => NamingService.makeAliases(n))
      .map(ns => withoutName.filter(p => ns.exists(n => p.localisation.matches(n))))
      .getOrElse(withoutName)

    SearchResult(req, allMatching)
  }

  override def findByName(name: String): Option[Tag] =
    search(TagSearchConf.ofName(name))
      .entities
      .find(_.localisation.name.contains(name))

}
