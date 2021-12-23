package com.lomicron.eu4.parsers.provinces

import com.lomicron.eu4.model.map.PositionRecord
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.map.GeographicRepository
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.resources.ResourceRepository

object ProvincePositionsParser {

  val arrayFields = Seq("position", "rotation", "height")

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
