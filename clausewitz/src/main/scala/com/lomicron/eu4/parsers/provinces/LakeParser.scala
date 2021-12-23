package com.lomicron.eu4.parsers.provinces

import com.lomicron.eu4.model.map.ElevatedLake
import com.lomicron.eu4.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.eu4.repository.api.map.GeographicRepository
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx, ObjectNodeEx}

object LakeParser {

  val lakeField = "lake"
  val arrayFields = Seq(lakeField)

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository = apply(repos.resources, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    val lakeConfs = files.getElevatedLakes
    val lakes = ClausewitzParser
      .parseFilesAsEntities(lakeConfs)
      .map(ClausewitzParser.fieldsToArrays(_, arrayFields))
      .flatMap(_.getArray(lakeField))
      .flatMap(_.toSeq)
      .flatMap(_.asObject)

    if (evalEntityFields)
      ConfigField.printCaseClass("ProvincePositions", lakes)

    val parsedLakes = lakes.map(ElevatedLake.fromJson)
    geography.map.createLakes(parsedLakes)

    geography
  }

}
