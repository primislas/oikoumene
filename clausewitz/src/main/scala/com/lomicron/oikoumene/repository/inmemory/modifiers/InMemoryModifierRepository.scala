package com.lomicron.oikoumene.repository.inmemory.modifiers

import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.repository.api.modifiers.StaticModifierRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository
import com.lomicron.utils.collection.CollectionUtils.MapEx

import scala.collection.immutable.SortedMap

case class InMemoryModifierRepository()
  extends InMemoryCrudRepository[String, Modifier](_.id)
    with StaticModifierRepository { self =>

  override def static: StaticModifierRepository =
    self.asInstanceOf[StaticModifierRepository]

  override def findNames(keys: Seq[String]): SortedMap[String, String] = {
    val m = find(keys)
      .filter(keyOf(_).isDefined)
      .groupBy(keyOf(_).get)
      .mapValues(_.head)
      .flatMapValues(v => v.localisation.flatMap(_.name).orElse(v.id))
    SortedMap[String, String]() ++ m
  }

  override def setId(entity: Modifier, id: String): Modifier = entity.copy(id = Some(id))

}
