package com.lomicron.oikoumene.repository.api

import scala.util.{Success, Try}

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

  def remove(key: Key): Try[Entity]

  def remove(keys: Seq[Key]): Seq[Try[Entity]] =
    keys.map(remove)

}
