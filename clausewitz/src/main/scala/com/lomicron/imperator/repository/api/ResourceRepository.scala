package com.lomicron.imperator.repository.api

import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.parsers.politics.TagConf
import com.lomicron.oikoumene.repository.api.resources.{GameFile, MapConfigResources}

trait ResourceRepository extends MapConfigResources {

  def getResource(fileConf: GameFile): GameFile
  def getLocalisation(language: String): Seq[LocalisationEntry]

  def getProvinceSetup: Seq[GameFile]
  def getBuildings: Seq[GameFile]
  def getPopTypes: Seq[GameFile]

  def getCountryTags: Option[GameFile]
  def getCountrySetup(config: Map[String, GameFile]): Seq[TagConf]

  object SupportedLanguages {
    val english = "english"
    val german = "german"
    val spanish = "spanish"
    val french = "french"
  }

}
