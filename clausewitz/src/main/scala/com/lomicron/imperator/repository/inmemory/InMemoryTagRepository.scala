package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.politics.Tag
import com.lomicron.imperator.repository.api.TagRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap

object InMemoryTagRepository extends InMemoryCrudRepository[String, Tag](t => Option(t.id)) with TagRepository {

  override def setId(tag: Tag, id: String): Tag = tag.copy(id = id)
  override def findNames(keys: Seq[String]): SortedMap[String, String] = ???

}
