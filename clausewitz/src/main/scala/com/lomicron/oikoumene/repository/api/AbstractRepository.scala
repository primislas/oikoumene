package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.search.{SearchConf, SearchResult}
import com.lomicron.utils.collection.CollectionUtils.OptionEx

import scala.collection.immutable.SortedMap
import scala.util.{Success, Try}

trait AbstractRepository[Key, Entity] {

  def create(entity: Entity): Entity

  def create(entities: Seq[Entity]): Seq[Entity] =
    entities.map(create)

  def update(entity: Entity): Entity

  def update(es: Seq[Entity]): Seq[Entity] =
    es.map(update)

  def upsert(entity: Entity): Entity =
    Try(update(entity)) match {
      case Success(e) => e
      case _ => create(entity)
    }

  def find(key: Key): Option[Entity]

  def find(keys: Seq[Key]): Seq[Entity] =
    keys.flatMap(find)

  def findAll: Seq[Entity]

  def findNames(keys: Seq[Key]): SortedMap[Key, String]

  def search(req: SearchConf): SearchResult[Entity] = {
    val entities = findAll.slice(req.offset, req.offset + req.size)
    SearchResult(req, entities)
  }

  def searchArgMatches[T](arg: Option[T], v: Option[T]): Boolean =
    arg.isEmpty || arg.contentsEqual(v)

  def remove(key: Key): Option[Entity]

  def remove(keys: Seq[Key]): Seq[Entity] =
    keys.flatMap(remove)

  def size: Int

}
