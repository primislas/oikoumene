package com.lomicron.eu4.parsers.modifiers

import com.lomicron.eu4.model.modifiers.Modifier
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.modifiers.ModifierRepository
import com.lomicron.eu4.repository.api.resources.ResourceRepository
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx

object ModifierParser {

  val baseTaxToTaxation: BigDecimal = 1
  val baseMpToManpower: BigDecimal = 0.250

  def apply(
             repos: RepositoryFactory,
             evalEntityFields: Boolean = false
           ): ModifierRepository =
    apply(repos.resources, repos.localisations, repos.modifiers, evalEntityFields)

  def apply
  (
    files: ResourceRepository,
    localisation: LocalisationRepository,
    modifiersRepo: ModifierRepository,
    evalEntityFields: Boolean
  ): ModifierRepository = {
    val eventModifiers = files.getEventModifiers
    val staticModifiers = files.getStaticModifiers
    val configs = eventModifiers ++ staticModifiers
    val modifiers = ClausewitzParser
      .parseFileFieldsAsEntities(configs)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printMapClass("Modifier", modifiers)

    modifiers.map(Modifier.fromJson).foreach(modifiersRepo.create)

    val staticsRepo = modifiersRepo.static
    staticsRepo.provincialTaxIncome.foreach(m => m.conf.setEx("local_tax_income", baseTaxToTaxation))
    staticsRepo.manpower.foreach(_.conf.setEx("local_manpower", baseMpToManpower))

    modifiersRepo
  }

}