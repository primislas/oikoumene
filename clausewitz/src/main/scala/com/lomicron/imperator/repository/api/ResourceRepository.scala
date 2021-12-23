package com.lomicron.imperator.repository.api

import com.lomicron.eu4.repository.api.resources.GameFile

trait ResourceRepository {

  def getResource(fileConf: GameFile): GameFile

  def getProvinceSetup: Seq[GameFile]

  def getBuildings: Seq[GameFile]
  def getPopTypes: Seq[GameFile]

}
