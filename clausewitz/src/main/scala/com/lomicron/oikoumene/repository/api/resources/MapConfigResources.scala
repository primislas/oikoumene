package com.lomicron.oikoumene.repository.api.resources

trait MapConfigResources {

  def getProvinceDefinitions: Option[String]

  def getAdjacencies: Option[String]

  def getProvinceTypes: Option[GameFile]

  def getProvincePositions: Option[String]

  def getAreas: Option[GameFile]

  def getRegions: Option[GameFile]

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

}
