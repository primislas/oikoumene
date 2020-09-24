package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.provinces.Building
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.repository.api.map.BuildingRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.patchFieldValue
import com.typesafe.scalalogging.LazyLogging

object BuildingParser extends LazyLogging {

  val manufactoryId = "manufactory"

  def apply(repos: RepositoryFactory): BuildingRepository =
    apply(repos.resources, repos.localisations, repos.buildings)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   buildingsRepo: BuildingRepository,
   evalEntityFields: Boolean = false): BuildingRepository = {

    val confs = files
      .getBuildings
      .map(ClausewitzParser.parse)
      .flatMap(o => {
        if (o._2.nonEmpty) logger.warn(s"Encountered ${o._2.size} errors while parsing religion: ${o._2}")
        o._1.fields.toStream
      })
      .map(e => e.getKey -> e.getValue).toMap
      .filterKeyValue((id, n) => {
        if (!n.isInstanceOf[ObjectNode])
          logger.warn(s"Expected building ObjectNode but at '$id' encountered ${n.toString}")
        n.isInstanceOf[ObjectNode]
      })
      .mapValues(_.asInstanceOf[ObjectNode])
      .mapKVtoValue((id, building) => patchFieldValue(building, idKey, TextNode.valueOf(id)))
      .mapKVtoValue((id, building) => localisation.findAndSetAsLocName(s"building_$id", building))
      .values


    val manufactoryNode = JsonMapper.textNode(manufactoryId)
    val buildings = confs.filter(c => !Option(c.get("id")).contains(manufactoryNode))

    val manufactory = confs
      .find(b => Option(b.get("id")).contains(manufactoryNode))
      .map(b => {
        b.remove("id")
        b
      })
    confs.filter(_.has(manufactoryId)).foreach(m => JsonMapper.patchMerge(m, manufactory))

    buildings
      .map(Building.fromJson)
      .foreach(buildingsRepo.create)
    buildingsRepo
  }


}
