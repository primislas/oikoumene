package com.lomicron.vicky.parsers.map

import com.lomicron.oikoumene.model.map.PositionRecord
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.vicky.repository.api.{GeographicRepository, RepositoryFactory, ResourceRepository}

object ProvincePositionsParser {

  val arrayFields: Seq[String] = Seq("position", "rotation", "height")

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository = apply(repos.resources, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    val provincePositions = files.getProvincePositions.get
    val positions = ClausewitzParser
      .parseFileFieldsAsEntities(Map("positions.txt" -> provincePositions))
      .map(ClausewitzParser.fieldsToArrays(_, arrayFields))

    if (evalEntityFields)
      ConfigField.printCaseClass("ProvincePositions", positions)

    val parsedPositions = positions.map(PositionRecord.fromJson).map(_.toConf)
    geography.map.updatePositions(parsedPositions)

    geography
  }

}
