package com.lomicron.imperator.parsers.map

import com.lomicron.imperator.repository.api.{GeographicRepository, ResourceRepository}
import com.lomicron.oikoumene.model.provinces.Area
import com.lomicron.oikoumene.parsers.ClausewitzParser.parseFileFieldsAsEntities
import com.lomicron.oikoumene.parsers.ConfigField
import com.lomicron.oikoumene.repository.api.map.AreaRepository
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository

object AreaParser {

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): AreaRepository = {

    val jsons = files
      .getAreas
      .map(parseFileFieldsAsEntities)
      .getOrElse(Seq.empty)

    if (evalEntityFields) ConfigField.printCaseClass("Area", jsons)

    val areaRepo = geography.areas
    jsons.map(Area.fromJson).foreach(areaRepo.create)
    areaRepo

  }

}
