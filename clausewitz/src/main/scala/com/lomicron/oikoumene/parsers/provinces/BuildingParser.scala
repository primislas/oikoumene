package com.lomicron.oikoumene.parsers.provinces

import com.lomicron.oikoumene.model.provinces.Building
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.BuildingRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.typesafe.scalalogging.LazyLogging

object BuildingParser extends LazyLogging {

  val idField = "id"
  val modifierField = "modifier"
  val manufactoryId = "manufactory"

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): BuildingRepository =
    apply(repos.resources, repos.localisations, repos.buildings, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    buildingsRepo: BuildingRepository,
    evalEntityFields: Boolean
  ): BuildingRepository = {

    val confs = ClausewitzParser
      .parseFileFieldsAsEntities(files.getBuildings)
      .map(localisation.setBuildingLocalisation)

    val buildings = confs.filterNot(c => c.getString(idField).contains(manufactoryId))
    val manufactory = confs
      .find(_.getString(idField).contains(manufactoryId))
      .map(_.removeEx(idField))
    confs.filter(_.has(manufactoryId)).foreach(m => JsonMapper.patchMerge(m, manufactory))

    if (evalEntityFields) {
      val modifiers = buildings.flatMap(_.getObject(modifierField))
      ConfigField.printCaseClass("Modifier", modifiers)
    }

    buildings
      .map(Building.fromJson)
      .foreach(buildingsRepo.create)
    buildingsRepo
  }

}
