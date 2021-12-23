package com.lomicron.eu4.repository.fs

import java.nio.file.Paths

import com.lomicron.eu4.io.FileIO
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.utils.json.JsonMapper

case class CacheWriter(repos: RepositoryFactory) {
  private val cacheDir = Paths.get(repos.settings.cacheDir.getOrElse("./cache"))

  def store: RepositoryFactory = {
    FileIO.ensureDirsExist(cacheDir)
    storeProvinces()
    storeTags()
    storeGeography()
    storeReligions()
    storeCultures()
    storeTrade()
//    storeLocalisation()
    // buildings
    // ideas
    // casus belli
    // diplomacy
    // war goals
    // war history

    repos
  }

  private def storeProvinces(): Unit =
    store(repos.provinces.findAll, CacheConstants.provinces)

  private def storeTags(): Unit =
    store(repos.tags.findAll, CacheConstants.tags)

  private def storeGeography(): Unit = {
    val g = repos.geography

    store(g.areas.findAll, CacheConstants.areas)
    store(g.regions.findAll, CacheConstants.regions)
    store(g.superregions.findAll, CacheConstants.superregions)
    store(g.continent.findAll, CacheConstants.continents)

    store(g.colonies.findAll, CacheConstants.colonies)
    store(g.climate.findAll, CacheConstants.climate)
    store(g.terrain.findAll, CacheConstants.terrain)

    val map = g.map
    store(map.mercator, CacheConstants.map)
    store(map.provincePositions, CacheConstants.positions)
    store(map.routes.values.toSeq.flatten.distinct, CacheConstants.routes)
    store(map.lakes, CacheConstants.elevatedLakes)
  }

  private def storeReligions(): Unit = {
    store(repos.religions.findAll, CacheConstants.religions)
    store(repos.religions.findAllGroups, CacheConstants.religionGroups)
  }

  private def storeCultures(): Unit = {
    store(repos.cultures.findAll, CacheConstants.cultures)
    store(repos.cultures.findAllGroups, CacheConstants.cultureGroups)
  }

  private def storeTrade(): Unit = {
    store(repos.tradeGoods.findAll, CacheConstants.tradeGoods)
    store(repos.tradeNodes.findAll, CacheConstants.tradeNodes)
  }

//  private def storeLocalisation(): Unit = {
//    store(repos.localisations.fetchAllEntries, CacheConstants.localisation)
//  }


  def store[T <: AnyRef](obj: T, fname: String): Unit = {
    FileIO.writeUTF(cacheDir, fname, JsonMapper.prettyPrint(obj))
  }


}
