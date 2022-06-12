package com.lomicron.imperator.parsers

import com.lomicron.imperator.model.politics.TagUpdate
import com.lomicron.imperator.parsers.provinces.ProvinceSetupParser
import com.lomicron.imperator.repository.api.RepositoryFactory
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

object SetupParser extends LazyLogging {

  def apply(repos: RepositoryFactory): RepositoryFactory = {
    val localisation = repos.localisation

    val files = repos
      .resources
      .getGameSetup
    val confs = ClausewitzParser.parseFilesAsEntities(files)

    val main = confs.head
    val provinces = main
      .getObject("provinces")
      .get
      .entrySeq()
      .flatMap(e => ClausewitzParser.merge(e.getValue).map(e.getKey -> _))
      .map(t => t._2.setEx(Fields.idKey, t._1))
      .map(localisation.setLocalisation)
      .map(ProvinceSetupParser.parseBuildings)
    ConfigField.printCaseClass("ProvinceUpdate", provinces)
    val countries = main
      .getObject("country")
      .flatMap(_.getObject("countries"))
      .get
      .entrySeq()
      .flatMap(e => ClausewitzParser.merge(e.getValue).map(e.getKey -> _))
      .map(t => t._2.setEx(Fields.idKey, t._1))
      .map(localisation.setLocalisation)
    ConfigField.printCaseClass("TagUpdate", countries)
    countries
      .map(TagUpdate.fromJson)
      .map(tu => {
        tu.ownControlCore
          .flatMap(repos.provinces.find)
          .map(p => {
            p.modify(_.state.owner).setTo(tu.id)
          })
          .foreach(repos.provinces.update)

        tu
      })
      .flatMap(tu =>
        repos
          .tags
          .find(tu.id)
          .map(_.withState(tu))
      )
      .foreach(repos.tags.update)

    logger.info(s"${confs.length} main confs")

    repos
  }

}
