package com.lomicron.oikoumene.repository.api

import scala.util.{Success, Try}
import com.lomicron.utils.collection.CollectionUtils._

trait AbstractRepository [Key, Entity] {

  def create(entity: Entity): Try[Entity]

  def create(entities: Seq[Entity]): Seq[Try[Entity]] =
    entities.map(create)

  def update(entity: Entity): Try[Entity]

  def upsert(entity: Entity): Try[Entity] =
    update(entity) match {
      case s: Success[Entity] => s
      case _ => create(entity)
    }

  def find(key: Key): Try[Entity]

  def find(keys: Seq[Key]): Seq[Entity] =
    keys.flatMap(find(_).toOption)

  def findAll: Seq[Entity]

  def search(req: SearchConf): SearchResult[Entity] = {
    val entities = findAll.slice(req.offset, req.offset + req.size)
    val totalEntities = size
    val quotient = totalEntities / req.size
    val remainder = totalEntities % req.size
    val totalPages = if (remainder == 0) quotient else quotient + 1
    SearchResult(req.page, req.size, totalPages, totalEntities, entities)
  }

  def searchArgMatches[T](arg: Option[T], v: Option[T]): Boolean =
    arg.isEmpty || arg.contentsEqual(v)

  def remove(key: Key): Try[Entity]

  def remove(keys: Seq[Key]): Seq[Try[Entity]] =
    keys.map(remove)

  def size: Int

}
