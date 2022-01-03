package com.lomicron.imperator.parsers.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.imperator.model.politics.Tag
import com.lomicron.imperator.repository.api.{RepositoryFactory, ResourceRepository, TagRepository}
import com.lomicron.oikoumene.parsers.ClausewitzParser.{Fields, parseAndLogErrors}
import com.lomicron.oikoumene.parsers.politics.TagConf
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.{GameFile, LocalisationRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx, textNode}
import com.typesafe.scalalogging.LazyLogging

import java.nio.file.Paths
import scala.collection.immutable.TreeMap
import scala.collection.parallel.CollectionConverters._
import scala.util.matching.Regex

object ImperatorTagParser extends LazyLogging {

  val tagPat: Regex = "^(?<tag>[A-Z]{3})\\s+=\\s+\"(?<relPath>[^\"]*)\"".r

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): TagRepository =
    apply(repos.resources, repos.localisation, repos.tags, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   tags: TagRepository,
   evalEntityFields: Boolean
  ): TagRepository = {

    val filesByTags = parseTagFileMapping(files)
    val tagConfs = files.getCountrySetup(filesByTags)
    val tagJsons = tagConfs
      .par
      .map(parseTag(_, files))
      .map(localisation.setLocalisation)
      .to(Seq)
    if (evalEntityFields)
      ConfigField.printCaseClass("Tag", tagJsons)
    tagJsons.map(Tag.fromJson).foreach(tags.create)

    tags
  }

  def parseTagFileMapping(files: ResourceRepository): Map[String, GameFile] =
    files
      .getCountryTags
      .flatMap(fc => fc.content.map(ClausewitzParser.parse).map(_._1))
      .flatMap(obj => obj.fields.toSeq.map(e => {
        if (e.getValue.isArray) {
          logger.warn(s"Tag ${e.getKey} has multiple declarations: ${e.getValue.toString}")
          (e.getKey, e.getValue.asArray.map(_.toSeq).get.last.asText)
        } else
          (e.getKey, e.getValue.asText)
      }))
      .getOrElse(Seq.empty)
      .map(kv => (kv._1, setupGameFile(kv._2)))
      .foldLeft(TreeMap[String, GameFile]())(_ + _)

  private def setupGameFile(path: String): GameFile = {
    val commonPath = Paths.get("game", path)
//    val relDir = commonPath.getParent.toString
//    GameFile.of(commonPath, relDir)
    GameFile.of(commonPath)
  }

  def parseTag(conf: TagConf, resources: ResourceRepository): ObjectNode = {
    if (conf.country.isEmpty)
      logger.warn(s"Tag ${conf.tag} has no country configuration")
    conf
      .country
      .map(resources.getResource)
      .flatMap(fc => fc.content.flatMap(parseAndLogErrors))
      .map(ClausewitzParser.removeEmptyObjects)
      .getOrElse(JsonMapper.objectNode)
      .setEx(Fields.idKey, textNode(conf.tag))
  }

}
