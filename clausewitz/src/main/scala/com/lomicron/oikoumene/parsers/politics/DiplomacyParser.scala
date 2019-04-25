package com.lomicron.oikoumene.parsers.politics

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.politics.DiploRelation
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.oikoumene.repository.api.politics.DiplomacyRepository
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

object DiplomacyParser extends LazyLogging {

  def apply(repos: RepositoryFactory): DiplomacyRepository = apply(repos.resources, repos.diplomacy)

  def apply
  (files: ResourceRepository,
   diplomacyRepo: DiplomacyRepository
  ): DiplomacyRepository = {

    val rels = files
      .getDiplomaticRelations
      .mapValues(ClausewitzParser.parse)
      .mapValues(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing religion: ${o._2}")
        parseRelationConfigFile(o._1)
      })
      .mapKVtoValue((filename, rels) => rels.map(_.setEx("source_file", filename)))
      .values.toList.flatten
      .map(DiploRelation.fromJson)

    val types = rels.map(_.`type`).distinct
    rels.foreach(diplomacyRepo.create)

    diplomacyRepo
  }

  def parseRelationConfigFile(rc: ObjectNode): Seq[ObjectNode] =
    rc.fieldNames().toSeq
      .flatMap(k => {
        val o = rc.getObject(k)
        parseRelation(k, o)
      })
      .map(parseHreEmperor)
      .map(parseCelestialEmperor)

  def parseRelation(k: String, o: Option[ObjectNode]): Option[ObjectNode] =
    ClausewitzParser
      .strToDateNode(k)
      .flatMap(d => o.map(_.setEx("date", d)))
      .orElse(o.map(_.setEx("type", JsonMapper.textNode(k))))

  def parseHreEmperor(o: ObjectNode): ObjectNode =
    if (o.has("emperor"))
      o.setEx("type", "emperor")
        .setEx("first", o.get("emperor"))
        .removeEx("emperor")
    else o

  def parseCelestialEmperor(o: ObjectNode): ObjectNode =
    if (o.has("celestial_emperor"))
      o.setEx("type", "celestial_emperor")
        .setEx("first", o.get("celestial_emperor"))
        .removeEx("celestial_emperor")
    else o

}
