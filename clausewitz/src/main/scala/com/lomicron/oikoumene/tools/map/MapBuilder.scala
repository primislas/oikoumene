package com.lomicron.oikoumene.tools.map

import java.nio.file.Paths

import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.io.FileIO
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.map.WorldMap
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.model.save.GamestateSave
import com.lomicron.oikoumene.model.save.tag.TagSave
import com.lomicron.oikoumene.repository.api.{GameFilesSettings, RepositoryFactory}
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.oikoumene.service.map.{MapBuilderSettings, SvgMapService}
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.softwaremill.quicklens._
import com.typesafe.scalalogging.LazyLogging

object MapBuilder extends LazyLogging {

  private val gameDir = "D:/Steam/steamapps/common/Europa Universalis IV"
  private val modsDir = FileResourceRepository.defaultModsDir
  private val saveGame = Paths.get(FileResourceRepository.defaultSaveDir, "autosave.eu4").toString
  val mods = Seq("MEIOUandTaxes1")

  def main(args: Array[String]) {
    logger.info("Starting the known world...")
    val cacheDir = Paths.get(modsDir, "map_rendering", "meiou").toString
    val rebuildCache = true
    val repos: RepositoryFactory = InMemoryRepositoryFactory(GameFilesSettings(gameDir, modsDir, mods, cacheDir, rebuildCache))
    Oikoumene.loadConfigs(repos)
//    val saveFile = FileIO.readSave(saveGame).get
//    val saveGamestate = SaveGameParser(saveFile)
//    val mapSvg = buildMap(repos, Some(saveGamestate))
    val mapSvg = buildMap(repos)
    writeMap(mapSvg)
  }

  def buildMap
  (
    repos: RepositoryFactory,
    mapSettings: MapBuilderSettings = MapBuilderSettings.default,
    save: Option[GamestateSave] = None
  ): String = {

    save.foreach(applySave(_, repos))
    val world = WorldMap(repos)
    val mapService = SvgMapService(repos)
    /*
      val globe = world.mercator.toSphere
      val initAzm = 3 * halfPI / 4
      val azms = (0 to 7).map(_ * initAzm)
      val polarOffset = halfPI / 8
      val polars = (-1 to 1).map(_ * polarOffset)

      val modes = Seq(MapModes.POLITICAL)
      val noNames = false
      val decimalPrecision = 2
      for {
        polarOffset <- polars
        azimuthOffset <- azms
        mode <- modes
      } yield {
        val fName = f"$polarOffset%.2f_$azimuthOffset%.2f_$mode.svg"
        val file = Paths.get(modsDir, "map_rendering", fName).toFile
        val rotation = SphericalCoord(0, polarOffset, azimuthOffset)
        val projection = globe.rotate(rotation).project
        val svg = mapService.worldSvg(WorldMap(projection, repos), MapModes.POLITICAL, noNames, decimalPrecision)
        FileIO.write(file, svg)
        logger.info(s"Produced $fName")
      }
    */

    mapService.worldSvg(world, mapSettings)
  }

  def applySave(save: GamestateSave, repos: RepositoryFactory): RepositoryFactory = {
    val provinces = repos.provinces
    val updated = save.provinces
      .flatMap(update => provinces
        .find(update.id).toOption
        .flatMap(p => update.owner.map(o => p.modify(_.history.state.owner).setTo(o)))
      )
    provinces.update(updated)

    val tags = repos.tags
    val updatedTags = save.countries
      .flatMap(update => {
        tags
          .find(update.id)
          .toOption
          .orElse(newTag(update))
          .map(tag => {
            var updated = tag
            updated = updated.modify(_.color).setTo(update.colors.mapColor)
            updated = update.name.map(name => updated.modify(_.localisation.name).setTo(name)).getOrElse(updated)
            updated
          })
      })
    tags.update(updatedTags)

    repos
  }

  def newTag(save: TagSave): Tag = {
    val l = Localisation.empty.modify(_.name).setTo(save.name)
    Tag(id = save.id, localisation = l)
  }

  def writeMap(mapSvg: String): Unit = {
    val mpDirPath = Paths.get(modsDir, "map_rendering")
    val fname = "mercator_political.svg"
    FileIO.writeUTF(mpDirPath, fname, mapSvg)
    logger.info(s"Produced $fname")
  }

}
