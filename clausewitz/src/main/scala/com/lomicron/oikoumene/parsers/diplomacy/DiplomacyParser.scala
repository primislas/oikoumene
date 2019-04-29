package com.lomicron.oikoumene.parsers.diplomacy

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.oikoumene.model.diplomacy.DiploRelation
import com.lomicron.oikoumene.model.diplomacy.DiploRelationType.{celestialEmperor, hreEmperor}
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.politics.DiplomacyRepository
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{textNode, _}
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
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing diplomatic history: ${o._2}")
        parseRelationConfigFile(o._1)
      })
      .mapKVtoValue((filename, rels) => rels.map(_.setEx("source_file", filename)))
      .values.toList.flatten

    val hre = rels.filter(_.get("type") == textNode(hreEmperor))
    if (hre.nonEmpty) hre.take(hre.size - 1)
      .foldRight(hre.last)((prev, last) => prev.setEx("end_date", last.get("start_date")))

    val celestial = rels.filter(_.get("type") == textNode(celestialEmperor))
    if (celestial.nonEmpty) celestial.take(celestial.size - 1)
      .foldRight(celestial.last)((prev, last) => prev.setEx("end_date", last.get("start_date")))

    ConfigField.printCaseClass("DiploRelation", rels)
    rels.map(DiploRelation.fromJson).foreach(diplomacyRepo.create)

    diplomacyRepo
  }

  def parseRelationConfigFile(rc: ObjectNode): Seq[ObjectNode] =
    rc.fields().toSeq
      .flatMap(e => {
        val (k, vs) = (e.getKey, e.getValue)
        val rels = vs match {
          case a: ArrayNode => a.toSeq.flatMap(_.asObject)
          case o: ObjectNode => Seq(o)
          case other =>
            logger.warn("Expected a diplo relation config but encountered: {}", other.asText())
            Seq.empty[ObjectNode]
        }
        rels.map(r => parseRelation(k, r))
      })
      .map(parseHreEmperor)
      .map(parseCelestialEmperor)

  def parseRelation(k: String, o: ObjectNode): ObjectNode =
    ClausewitzParser
      .strToDateNode(k)
      .map(d => o.setEx("start_date", d))
      .getOrElse(o.setEx("type", k))

  def parseHreEmperor(o: ObjectNode): ObjectNode =
    if (o.has(hreEmperor))
      o.setEx("type", hreEmperor)
        .setEx("first", o.get(hreEmperor))
        .removeEx(hreEmperor)
    else o

  def parseCelestialEmperor(o: ObjectNode): ObjectNode =
    if (o.has(celestialEmperor))
      o.setEx("type", celestialEmperor)
        .setEx("first", o.get(celestialEmperor))
        .removeEx(celestialEmperor)
    else o

}
