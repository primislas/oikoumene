package com.lomicron.oikoumene.repository.api.resources

import java.nio.file.Path

import com.lomicron.oikoumene.io.FileNameAndContent
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
  def getCountryHistory: Map[String, FileNameAndContent]

  def getDiplomaticRelations: Map[String, String]

  def getWarHistory: Map[String, String]

  def getCasusBelliTypes: Map[String, String]

  def getWarGoalTypes: Map[String, String]

  def getProvinceDefinitions: Option[String]

  def getAdjacencies: Option[String]

  def getProvinceHistory: Map[Int, FileNameAndContent]

  def getBuildings: Map[String, String]

  def getProvinceTypes: Map[String, String]

  def getProvincePositions: Option[String]

  def getAreas: Option[String]

  def getRegions: Option[String]

  def getSuperregions: Option[String]

  def getContinents: Option[String]

  def getColonialRegions: Option[String]

  def getTerrain: Map[String, String]

  def getClimate: Option[String]

  def getElevatedLakes: Map[String, String]

  def getProvinceMap: Option[Path]

  def getTerrainMap: Option[Path]

  def getHeightMap: Option[Path]

  def getRiversMap: Option[Path]

  def getBackground(season: String): Option[Path]

  def isMapModded: Boolean

  def getCultures: Option[String]

  def getReligions: Map[String, String]

  def getGovernments: Map[String, String]

  def getGovernmentRanks: Map[String, String]

  def getGovernmentReforms: Map[String, String]

  def getTechnologies: Map[String, String]

  def getTechGroupConfig: Option[String]

  def getIdeas: Map[String, String]

  def getPolicies: Map[String, String]

  def getTradeGoods: Map[String, String]

  def getPrices: Map[String, String]

  def getTradeNodes: Map[String, String]

  def getCentersOfTrade: Map[String, String]

  def getEventModifiers: Map[String, String]

  def getStaticModifiers: Map[String, String]

  object SupportedLanguages {
    val english = "english"
    val german = "german"
    val spanish = "spanish"
    val french = "french"
  }

}
