package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.repository.api.AbstractRepository
import com.lomicron.oikoumene.repository.api.resources.{GameFile, LocalisationRepository}

object EntityParser {

//  def parseFileFieldsAsEntities[K, R <: AbstractRepository[K, Entity]]
//  (
//    f: Option[GameFile],
//    repo: R,
//    serializer: ObjectNode => Entity,
//    localisation: Option[LocalisationRepository] = None,
//    evalEntityFields: Boolean = false,
//    entityName: Option[String]
//  ): R =
//    parseFileFieldsAsEntities[K, R](f.toSeq, repo, serializer, localisation, evalEntityFields, entityName)
//
//  def parseFileFieldsAsEntities[K, R <: AbstractRepository[K, Entity]]
//  (
//    fs: Seq[GameFile],
//    repo: R,
//    serializer: ObjectNode => Entity,
//    localisation: Option[LocalisationRepository] = None,
//    evalEntityFields: Boolean = false,
//    entityName: Option[String]
//  ): R =
//    parseEntities[K, R](fs, repo, ClausewitzParser.parseFileFieldsAsEntities, serializer, localisation, evalEntityFields, entityName)
//
//  def parseFilesAsEntities[K, R <: AbstractRepository[K, Entity]]
//  (
//    fs: Seq[GameFile],
//    repo: R,
//    serializer: ObjectNode => Entity,
//    localisation: Option[LocalisationRepository] = None,
//    evalEntityFields: Boolean = false,
//    entityName: Option[String]
//  ): R =
//    parseEntities[K, R](fs, repo, ClausewitzParser.parseFilesAsEntities, serializer, localisation, evalEntityFields, entityName)

  def parseEntities[K, E <: Entity, R <: AbstractRepository[K, E]]
  (
    fs: Seq[GameFile],
    repo: R,
    parser: Seq[GameFile] => Seq[ObjectNode],
    deserializer: JsonNode => E,
    localisation: Option[LocalisationRepository] = None,
    evalEntityFields: Boolean = false,
    entityName: Option[String] = None,
  ): R = {

    val jsons = parser(fs)
    localisation
      .foreach(l => jsons.foreach(l.setLocalisation))
    if (evalEntityFields) {
      val className = entityName.getOrElse("ClassName")
      ConfigField.printCaseClass(className, jsons)
    }
    jsons.map(deserializer).foreach(repo.create)

    repo
  }

}
