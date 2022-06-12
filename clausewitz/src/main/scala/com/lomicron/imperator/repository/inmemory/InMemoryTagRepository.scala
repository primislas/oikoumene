package com.lomicron.imperator.repository.inmemory

import com.lomicron.imperator.model.politics.Tag
import com.lomicron.imperator.repository.api.TagRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap
import scala.collection.mutable

object InMemoryTagRepository extends InMemoryCrudRepository[String, Tag](t => Option(t.id)) with TagRepository { self =>

  private val tagsByProvId: mutable.Map[Int, Tag] = mutable.Map()

  override def setId(tag: Tag, id: String): Tag = tag.copy(id = id)
  override def findNames(keys: Seq[String]): SortedMap[String, String] = ???


  override def create(entities: Seq[Tag]): Seq[Tag] = {
    updateProvOwnership(entities)
    super.create(entities)
  }


  override def update(entity: Tag): Tag = {
    updateProvOwnership(Seq(entity))
    super.update(entity)
  }

  override def update(es: Seq[Tag]): Seq[Tag] = {
    updateProvOwnership(es)
    super.update(es)
  }

  private def updateProvOwnership(es: Seq[Tag]): TagRepository = {
    es
      .foreach(tag => tag.state.foreach(state => {
        state.ownControlCore.foreach(provId => tagsByProvId.put(provId, tag))
      }))

    self
  }

  override def ownerOfProvince(provId: Int): Option[Tag] =
    tagsByProvId.get(provId)

  override def distinctProvinceOwners: Seq[Tag] =
    tagsByProvId.values.toList.distinct

}
