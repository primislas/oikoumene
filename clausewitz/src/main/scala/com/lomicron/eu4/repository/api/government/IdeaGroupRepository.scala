package com.lomicron.eu4.repository.api.government

import com.lomicron.eu4.model.government.IdeaGroup
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait IdeaGroupRepository extends AbstractRepository[String, IdeaGroup] {
  def ofTag(tagId: String): Option[IdeaGroup] =
    find(s"${tagId}_ideas")
}
