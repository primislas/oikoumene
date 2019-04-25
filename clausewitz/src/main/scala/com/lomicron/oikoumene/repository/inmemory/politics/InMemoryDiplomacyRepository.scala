package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.DiploRelation
import com.lomicron.oikoumene.repository.api.politics.DiplomacyRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

case class InMemoryDiplomacyRepository()
  extends InMemoryCrudRepository[Int, DiploRelation](_.id.getOrElse(0))
    with DiplomacyRepository {

  // TODO also set up tag relations upon repo updates
  override def tagRelations(tag: String): Seq[DiploRelation] = ???

}
