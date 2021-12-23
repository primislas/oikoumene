package com.lomicron.eu4.parsers.provinces

import com.lomicron.eu4.model.provinces.ProvinceTypes
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.map.GeographicRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}

object ProvinceTypeParser {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository = apply(repos.resources, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean): GeographicRepository = {

    val provinceTypes = files.getProvinceTypes.toSeq // default.map
    val types = ClausewitzParser.parseFilesAsEntities(provinceTypes)

    if (evalEntityFields)
      ConfigField.printCaseClass("ProvinceTypes", types)

    val parsedTypes = types.headOption.map(ProvinceTypes.fromJson).getOrElse(ProvinceTypes.empty)
    geography.provinceTypes(parsedTypes)
  }

}
