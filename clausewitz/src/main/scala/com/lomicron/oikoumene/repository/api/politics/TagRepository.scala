package com.lomicron.oikoumene.repository.api.politics

import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TagRepository extends AbstractRepository[String, Tag] {
  def findByName(name: String): Option[Tag]
}
