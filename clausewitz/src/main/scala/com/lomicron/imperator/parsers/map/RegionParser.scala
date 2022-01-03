package com.lomicron.imperator.parsers.map

import com.lomicron.imperator.repository.api.{GeographicRepository, ResourceRepository}
import com.lomicron.oikoumene.model.provinces.Region
import com.lomicron.oikoumene.parsers.ClausewitzParser.parseFileFieldsAsEntities
import com.lomicron.oikoumene.parsers.ConfigField
import com.lomicron.oikoumene.repository.api.map.RegionRepository
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository

object RegionParser {

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean
  ): RegionRepository = {

    val jsons = files.getRegions.map(parseFileFieldsAsEntities).getOrElse(Seq.empty)
    if (evalEntityFields) ConfigField.printCaseClass("Region", jsons)

    val regions = geography.regions
    jsons.map(Region.fromJson).foreach(regions.create)
    regions
  }

}
