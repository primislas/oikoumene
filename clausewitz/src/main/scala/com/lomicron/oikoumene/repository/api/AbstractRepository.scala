package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.repository.api.search.{SearchConf, SearchResult}
import com.lomicron.utils.collection.CollectionUtils._

import scala.collection.immutable.SortedMap
import scala.util.{Success, Try}

trait AbstractRepository [Key, Entity] {

  def create(entity: Entity): Try[Entity]

  def create(entities: Seq[Entity]): Seq[Try[Entity]] =
    entities.map(create)

  def update(entity: Entity): Try[Entity]

  def update(es: Seq[Entity]): Seq[Try[Entity]] =
    es.map(update)

  def upsert(entity: Entity): Try[Entity] =
    update(entity) match {
      case s: Success[Entity] => s
      case _ => create(entity)
    }

  def find(key: Key): Try[Entity]

  def find(keys: Seq[Key]): Seq[Entity] =
    keys.flatMap(find(_).toOption)

  def findAll: Seq[Entity]

  def findNames(keys: Seq[Key]): SortedMap[Key, String]

  def search(req: SearchConf): SearchResult[Entity] = {
    val entities = findAll.slice(req.offset, req.offset + req.size)
    SearchResult(req, entities)
  }

  def searchArgMatches[T](arg: Option[T], v: Option[T]): Boolean =
    arg.isEmpty || arg.contentsEqual(v)

  def remove(key: Key): Try[Entity]

  def remove(keys: Seq[Key]): Seq[Try[Entity]] =
    keys.map(remove)

  def size: Int

}
