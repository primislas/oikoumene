package com.lomicron.eu4.parsers.politics

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.eu4.model.politics.{Culture, CultureGroup}
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.politics.CultureRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper.{arrayNodeOf, patchFieldValue}
import com.typesafe.scalalogging.LazyLogging

object CultureParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : CultureRepository =
    apply(repos.resources, repos.localisations, repos.cultures, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   cultureRepo: CultureRepository,
   evalEntityFields: Boolean): CultureRepository = {

    val groupCultures = files
      .getCultures
      .map(ClausewitzParser.parse)
      .map(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing cultures: ${o._2}")
        o._1.fields.toStream
      })
      .getOrElse(LazyList.empty)
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((id, n) => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected culture group ObjectNode but at '$id' encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValuesEx(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, religionGroup) => patchFieldValue(religionGroup, idKey, TextNode.valueOf(id)))
      .mapKVtoValue(localisation.findAndSetAsLocName)
      .values.toSeq
      .map(parseCultures)
      .map { case (group: ObjectNode, cultures: Seq[ObjectNode]) =>
        cultures.foreach(c => localisation.findAndSetAsLocName(c.get("id").asText(), c))
        (group, cultures)
      }

    val cultures = groupCultures.flatMap(_._2)
    val groups = groupCultures.map(_._1)

    if (evalEntityFields) {
      ConfigField.printCaseClass("Culture", cultures)
      ConfigField.printCaseClass("CultureGroup", groups)
    }

    groups.map(CultureGroup.fromJson).foreach(cultureRepo.createGroup)
    cultures.map(Culture.fromJson).foreach(cultureRepo.create)

    cultureRepo
  }

  private def parseCultures(cultureGroup: ObjectNode): (ObjectNode, Seq[ObjectNode]) = {
    val cultures = cultureGroup.fields.toStream
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((f, _) => isCultureField(f))
      .mapValuesEx(culture => culture.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, culture) => patchFieldValue(culture, idKey, TextNode.valueOf(id)))
      .values
      .map(culture => patchFieldValue(culture, "culture_group_id", cultureGroup.get("id")))
      .toSeq
    cultures.map(_.get("id")).map(_.asText()).foreach(cultureGroup.remove)
    val idsArray = arrayNodeOf(cultures.map(_.get("id")))
    cultureGroup.set("culture_ids", idsArray)
    (cultureGroup, cultures)
  }

  private val cultureGroupFields = Set("id", "localisation",
    "culture_ids", "graphical_culture", "male_names",
    "female_names", "dynasty_names", "second_graphical_culture")

  private def isCultureField(field: String) = !cultureGroupFields.contains(field)

}
