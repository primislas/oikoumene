package com.lomicron.oikoumene.repository.api.politics

import com.lomicron.oikoumene.model.diplomacy.DiploRelation
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait DiplomacyRepository extends AbstractRepository[Int, DiploRelation] {

  def tagRelations(tag: String): Seq[DiploRelation]

}
