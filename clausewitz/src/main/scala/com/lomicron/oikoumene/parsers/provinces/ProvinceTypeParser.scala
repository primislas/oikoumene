package com.lomicron.oikoumene.parsers.provinces

import com.lomicron.oikoumene.model.provinces.ProvinceTypes
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.resources.ResourceRepository

object ProvinceTypeParser {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository = apply(repos.resources, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    val provinceTypes = files.getProvinceTypes // default.map
    val types = ClausewitzParser.parseFilesAsEntities(provinceTypes)

    if (evalEntityFields)
      ConfigField.printCaseClass("ProvinceTypes", types)

    val parsedTypes = types.headOption.map(ProvinceTypes.fromJson).getOrElse(ProvinceTypes.empty)
    geography.provinceTypes(parsedTypes)
  }

}
