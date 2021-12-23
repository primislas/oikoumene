package com.lomicron.eu4.repository.api.politics

import com.lomicron.eu4.model.politics.Tag
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TagRepository extends AbstractRepository[String, Tag] {
  def findByName(name: String): Option[Tag]
}
