package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.model.localisation.LocalisationEntry

trait ResourceRepository {

  def getLocalisation: Seq[LocalisationEntry] =
    getLocalisation(SupportedLanguages.english)

  def getLocalisation(language: String): Seq[LocalisationEntry]

  /**
    *
    * @return country files by country tags
    */
  def getCountryTags: Map[String, String]

  /**
    *
    * @return country file content by country tag
    */
  def getCountries(filesByTags: Map[String, String]): Map[String, String]

  /**
    *
    *
    * @return country history by country tag
    */
  def getCountryHistory: Map[String, String]

  def getDiplomaticRelations: Map[String, String]

  def getWarHistory: Map[String, String]

  def getCasusBelliTypes: Map[String, String]

  def getWarGoalTypes: Map[String, String]

  def getProvinceDefinitions: Option[String]

  def getProvinceHistory: Map[Int, String]

  def getBuildings: Seq[String]

  def getProvinceTypes: Option[String]

  def getProvincePositions: Option[String]

  def getAreas: Option[String]

  def getRegions: Option[String]

  def getSuperregions: Option[String]

  def getContinents: Option[String]

  def getColonialRegions: Option[String]

  def getTerrain: Option[String]

  def getClimate: Option[String]

  def getCultures: Option[String]

  def getReligions: Option[String]

  object SupportedLanguages {
    val english = "english"
    val german = "german"
    val spanish = "spanish"
    val french = "french"
  }

}