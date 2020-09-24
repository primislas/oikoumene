package com.lomicron.oikoumene.repository.fs

import java.nio.charset.StandardCharsets
import java.nio.file.Paths

import com.lomicron.oikoumene.model.map.{ElevatedLake, MercatorMap, ProvincePositions, Route}
import com.lomicron.oikoumene.model.politics._
import com.lomicron.oikoumene.model.provinces._
import com.lomicron.oikoumene.model.trade.{TradeGood, TradeNode}
import com.lomicron.oikoumene.parsers.localisation.LocalisationParser
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.io.IO
import com.lomicron.utils.json.JsonMapper
import com.typesafe.scalalogging.LazyLogging

case class CacheReader(repos: RepositoryFactory) extends LazyLogging {
  private var cacheDir = Paths.get("./cache")

  def load: Option[RepositoryFactory] = {
    if (!repos.settings.rebuildCache)
      repos
        .settings
        .cacheDir
        .filter(cacheHasAllFiles)
        .map(cache => {
          cacheDir = Paths.get(cache)
          logger.info("Loading cached data...")
          LocalisationParser(repos)
          readProvinces()
          readTags()
          readGeography()
          readReligions()
          readCultures()
          readTrade()
          // buildings
          // ideas
          // casus belli
          // diplomacy
          // war goals
          // war history
          logger.info("Loaded cached data")

          repos
        })
    else
      None
  }

  private def cacheHasAllFiles(dir: String): Boolean = {
    val files = IO.listFiles(dir).toSet
    val missingFiles = CacheConstants.requiredFiles.map(f => Paths.get(dir, f).toString).toSet -- files
    if (missingFiles.nonEmpty)
      logger.warn(s"Cache is missing the following files: ${JsonMapper.toJson(missingFiles)}")
    missingFiles.isEmpty
  }

  private def readProvinces(): Unit = {
    val provs = read[Seq[Province]](CacheConstants.provinces).map(_.atStart)
    repos.provinces.update(provs)
  }

  private def readTags(): Unit = {
    val tags = read[Seq[Tag]](CacheConstants.tags).map(_.atStart)
    repos.tags.update(tags)
  }

  private def readGeography(): Unit = {
    val g = repos.geography

    readAndUpdate[Seq[Area]](CacheConstants.areas, es => g.areas.update(es))
    readAndUpdate[Seq[Region]](CacheConstants.areas, es => g.regions.update(es))
    readAndUpdate[Seq[SuperRegion]](CacheConstants.areas, es => g.superregions.update(es))
    readAndUpdate[Seq[Continent]](CacheConstants.areas, es => g.continent.update(es))

    readAndUpdate[Seq[ColonialRegion]](CacheConstants.colonies, es => g.colonies.update(es))
    readAndUpdate[Seq[Climate]](CacheConstants.climate, es => g.climate.update(es))
    readAndUpdate[Seq[Terrain]](CacheConstants.terrain, es => g.terrain.update(es))

    val map = g.map
    readAndUpdate[MercatorMap](CacheConstants.map, map.updateMercator)
    readAndUpdate[Seq[ProvincePositions]](CacheConstants.positions, es => map.updatePositions(es))
    readAndUpdate[Seq[Route]](CacheConstants.routes, es => map.updateRoutes(es))
    readAndUpdate[Seq[ElevatedLake]](CacheConstants.elevatedLakes, es => map.createLakes(es))
  }

  private def readReligions(): Unit = {
    readAndUpdate[Seq[Religion]](CacheConstants.religions, repos.religions.update)
    readAndUpdate[Seq[ReligionGroup]](CacheConstants.religionGroups, repos.religions.createGroups)
  }

  private def readCultures(): Unit = {
    readAndUpdate[Seq[Culture]](CacheConstants.cultures, repos.cultures.update)
    readAndUpdate[Seq[CultureGroup]](CacheConstants.cultureGroups, repos.cultures.createGroups)
  }

  private def readTrade(): Unit = {
    readAndUpdate[Seq[TradeGood]](CacheConstants.tradeGoods, repos.tradeGoods.create)
    readAndUpdate[Seq[TradeNode]](CacheConstants.tradeNodes, repos.tradeNodes.create)
  }

//  private def storeLocalisation(): Unit = {
//    repos.localisations
//    readAndUpdate[Seq[LocalisationEntry]](CacheConstants.localisation, repos.localisations.update)
//  }


  def read[T : Manifest](fname: String): T = {
    val path = cacheDir.resolve(fname)
    val content = IO.readTextFile(path.toString, StandardCharsets.ISO_8859_1)
    JsonMapper.fromJson[T](content)
  }

  def readAndUpdate[T : Manifest](fname: String, updateF: T => AnyRef): AnyRef =
    updateF(read[T](fname))

}
