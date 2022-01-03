package com.lomicron.oikoumene.parsers.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.eu4.model.provinces.Province
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx

import scala.util.matching.Regex

object MapConfigParser {

  val provinceDefinitionPat: Regex =
    "^(?<id>\\d+);(?<red>\\d+);(?<green>\\d+);(?<blue>\\d+);(?<comment>[^;]*)(?:;(?<tag>.*))?".r
  val addBuildingField = "add_building"
  val removeBuildingField = "remove_building"

  case class Prov(id: Int, conf: ObjectNode) { self =>
    def update(f: ObjectNode => ObjectNode): Prov = {
      f(conf)
      self
    }
  }

  def parseProvinceDefinitions(definitions: Option[String]): Seq[Province] =
    definitions
      .map(_.linesIterator.toSeq)
      .getOrElse(Seq.empty)
      .flatMap(parseDefinition)

  def parseDefinition(line: String): Option[Province] =
    line match {
      case provinceDefinitionPat(id, r, g, b, comment) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment))
      case provinceDefinitionPat(id, r, g, b, comment, tag2) =>
        Some(Province(id.toInt, Color(r.toInt, g.toInt, b.toInt), comment, tag2))
      case _ => None
    }

  def addLocalisation(p: Prov, localisation: LocalisationRepository): Prov =
    localisation
      .fetchProvince(p.id)
      .map(loc => p.update(_.setEx(Fields.localisation, loc)))
      .getOrElse(p)

}
