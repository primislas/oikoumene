package com.lomicron.eu4.parsers.diplomacy

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.eu4.model.diplomacy.DiploRelation
import com.lomicron.eu4.model.diplomacy.DiploRelationType.{celestialEmperor, hreEmperor}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.diplomacy.DiplomacyRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{textNode, _}
import com.typesafe.scalalogging.LazyLogging

object DiplomacyParser extends LazyLogging {

  def apply(repos: RepositoryFactory): DiplomacyRepository = apply(repos.resources, repos.diplomacy)

  def apply
  (files: ResourceRepository,
   diplomacyRepo: DiplomacyRepository,
   evalEntityFields: Boolean = false
  ): DiplomacyRepository = {

    val relConfigs = files.getDiplomaticRelations
    val confFiles = ClausewitzParser.parseFilesAsEntities(relConfigs)
    val rels = confFiles.flatMap(cf => {
      val filename = Option(cf.get("source_file")).getOrElse(nullNode)
      cf.remove("source_file")
      parseRelationConfigFile(cf).map(_.setEx("source_file", filename))
    })

    val hre = rels.filter(_.get("type") == textNode(hreEmperor))
    if (hre.nonEmpty) hre.take(hre.size - 1)
      .foldRight(hre.last)((prev, last) => prev.setEx("end_date", last.get("start_date")))

    val celestial = rels.filter(_.get("type") == textNode(celestialEmperor))
    if (celestial.nonEmpty) celestial.take(celestial.size - 1)
      .foldRight(celestial.last)((prev, last) => prev.setEx("end_date", last.get("start_date")))

    if (evalEntityFields)
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
    if (ClausewitzParser.isDate(k)) o.setEx("start_date", k)
    else o.setEx("type", k)

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
