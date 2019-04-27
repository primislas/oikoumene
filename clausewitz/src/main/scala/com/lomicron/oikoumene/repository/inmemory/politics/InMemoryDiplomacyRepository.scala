package com.lomicron.oikoumene.repository.inmemory.politics

import com.lomicron.oikoumene.model.diplomacy.DiploRelation
import com.lomicron.oikoumene.repository.api.politics.DiplomacyRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.mutable
import scala.util.Try

case class InMemoryDiplomacyRepository()
  extends InMemoryCrudRepository[Int, DiploRelation](_.id)
    with DiplomacyRepository {

  private val relsByTag = new mutable.HashMap[String, mutable.TreeSet[Int]]()
  private var idsSeq = 0

  override def nextId: Option[Int] = {
    idsSeq = idsSeq + 1
    Option(idsSeq)
  }

  override def setId(entity: DiploRelation, id: Int): DiploRelation =
    entity.copy(id = Option(id))

  override def create(entity: DiploRelation): Try[DiploRelation] = super
    .create(entity)
    .map(addRelsByTag)

  override def tagRelations(tag: String): Seq[DiploRelation] =
    relsByTag.get(tag).map(_.toSeq).getOrElse(Seq.empty).flatMap(find(_).toOption)

  override def remove(key: Int): Try[DiploRelation] = super
    .remove(key)
    .map(removeRelsFromTag)

  private def addRelsByTag(rel: DiploRelation): DiploRelation = {
    rel.id.foreach(addRelToTag(_, rel.first))
    rel.id.foreach(id => rel.second.foreach(addRelToTag(id, _)))
    rel
  }

  private def addRelToTag(id: Int, tag: String): Unit = {
    relsByTag.getOrElseUpdate(tag, new mutable.TreeSet[Int]()).add(id)
  }

  private def removeRelsFromTag(rel: DiploRelation): DiploRelation = {
    for { id <- rel.id } removeRelFromTag(id, rel.first)
    for { id <- rel.id; tag <- rel.second } removeRelFromTag(id, tag)

    rel
  }

  private def removeRelFromTag(id: Int, tag: String): Unit = {
    relsByTag.get(tag).foreach(_.remove(id))
  }

}
