package com.lomicron.vicky.parsers

import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper.{JsonNodeEx, ObjectNodeEx}
import com.lomicron.vicky.model.production.{Building, ProductionType}
import com.lomicron.vicky.repository.api.{BuildingRepository, ProductionTypeRepository, RepositoryFactory, ResourceRepository}
import com.typesafe.scalalogging.LazyLogging

object ProductionTypeParser extends LazyLogging {

  def apply(repos: RepositoryFactory, evalEntityFields: Boolean = false): ProductionTypeRepository =
    apply(repos.resources, repos.localisations, repos.productionTypes, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    productionTypeRepo: ProductionTypeRepository,
    evalEntityFields: Boolean
  ): ProductionTypeRepository = {

    val productionTypes = ClausewitzParser
      .parseFileFieldsAsEntities(files.getProductionTypes)
      .map(localisation.setLocalisation)

    if (evalEntityFields) {
      ConfigField.printCaseClass("ProductionType", productionTypes)
      val popConfs = productionTypes
        .flatMap(pt => pt.getSeq("owner") ++ pt.getSeq("employees"))
        .flatMap(_.asObject)
      ConfigField.printCaseClass("Pop", popConfs)
    }

    productionTypes
      .map(ProductionType.fromJson)
      .foreach(productionTypeRepo.create)
    productionTypeRepo
  }

}
