package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.repository.api.politics.TagRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

case class InMemoryTagRepository()
  extends InMemoryEntityRepository[Tag]
  with TagRepository {

  override def setId(entity: Tag, id: String): Tag = entity.copy(id = id)

}
