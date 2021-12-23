package com.lomicron.eu4.repository.api.diplomacy

import com.lomicron.eu4.model.diplomacy.DiploRelation
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait DiplomacyRepository extends AbstractRepository[Int, DiploRelation] {

  def tagRelations(tag: String): Seq[DiploRelation]

}
