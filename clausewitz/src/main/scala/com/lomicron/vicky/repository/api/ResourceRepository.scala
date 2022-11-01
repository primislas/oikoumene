package com.lomicron.vicky.repository.api

import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.parsers.politics.TagFileConf
import com.lomicron.oikoumene.repository.api.resources.GameFile

trait ResourceRepository {

  def getResource(fileConf: GameFile): GameFile

  def getLocalisation: Seq[LocalisationEntry] =
    getLocalisation(SupportedLanguages.english)

  def getLocalisation(language: String): Seq[LocalisationEntry]

  /**
    *
    * @return country files by country tags
    */
  def getCountryTags: Seq[GameFile]
  /**
    *
    * @return country file content by country tag
    */
  def getCountries(filesByTags: Map[String, GameFile]): Map[String, GameFile]
  /**
    *
    *
    * @return country history by country tag
    */
  def getCountryHistory: Map[String, GameFile]
  def getCountryConfigs(filesByTag: Map[String, GameFile]): Seq[TagFileConf]
  def getRulerPersonalities: Seq[GameFile]

  def getDiplomaticRelations: Seq[GameFile]
  def getWarHistory: Seq[GameFile]
  def getCasusBelliTypes: Seq[GameFile]
  def getWarGoalTypes: Seq[GameFile]

  def getProvinceDefinitions: Option[String]
  def getAdjacencies: Option[String]
  def getProvinceHistoryResources: Map[Int, GameFile]
  def getProvinceHistory: Map[Int, GameFile]
  def getBuildings: Seq[GameFile]

  def getProvinceTypes: Option[GameFile]
  def getProvincePositions: Option[String]
  def getAreas: Option[String]
  def getRegions: Option[String]
  def getSuperregions: Option[String]
  def getContinents: Option[String]
  def getColonialRegions: Option[String]
  def getTerrain: Option[GameFile]
  def getClimate: Option[String]
  def getElevatedLakes: Seq[GameFile]
  def getProvinceMap: Option[GameFile]
  def getTerrainMap: Option[GameFile]
  def getHeightMap: Option[GameFile]
  def getRiversMap: Option[GameFile]
  def getBackground(season: String): Option[GameFile]
  def isMapModded: Boolean

  def getCultures: Option[String]
  def getReligions: Seq[GameFile]

  def getGovernments: Seq[GameFile]
  def getGovernmentRanks: Seq[GameFile]
  def getGovernmentReforms: Seq[GameFile]
  def getTechnologies: Seq[GameFile]
  def getTechGroupConfig: Option[String]
  def getInventions: Seq[GameFile]
  def getIdeas: Seq[GameFile]
  def getPolicies: Seq[GameFile]
  def getStateEdicts: Seq[GameFile]

  def getTradeGoods: Seq[GameFile]
  def getPrices: Seq[GameFile]
  def getTradeNodes: Seq[GameFile]
  def getCentersOfTrade: Seq[GameFile]

  def getEventModifiers: Seq[GameFile]
  def getStaticModifiers: Seq[GameFile]

  object SupportedLanguages {
    val english = "english"
    val german = "german"
    val spanish = "spanish"
    val french = "french"
  }

}
