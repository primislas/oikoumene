package com.lomicron.oikoumene.tools.map

import java.nio.file.Paths

import com.lomicron.oikoumene.engine.Oikoumene
import com.lomicron.oikoumene.io.FileIO
import com.lomicron.oikoumene.model.map.{MapModes, WorldMap}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.inmemory.InMemoryRepositoryFactory
import com.lomicron.oikoumene.writers.map.SvgMapService
import com.typesafe.scalalogging.LazyLogging
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.geometry.Geometry.halfPI
import com.lomicron.utils.geometry.{Geometry, SphericalCoord}

object MapBuilder extends LazyLogging {

  val gameDir = "D:\\Steam\\steamapps\\common\\Europa Universalis IV"
  val modsDir = s"${System.getProperty("user.home")}\\Documents\\Paradox Interactive\\Europa Universalis IV\\mod"

  def main(args: Array[String]) {
    logger.info("Starting the known world...")
    val repos: RepositoryFactory = InMemoryRepositoryFactory(gameDir, modsDir)
    Oikoumene.parseConfigs(repos)
    buildMap(repos)
  }

  def buildMap(repos: RepositoryFactory): Unit = {
    val world = WorldMap(repos)
    val mapService = SvgMapService(repos)
    val mpSvg = mapService.worldSvg(world, MapModes.POLITICAL)
    val mpFPath = Paths.get(modsDir, "map_rendering", "mercator_political.svg").toFile
    FileIO.write(mpFPath, mpSvg)
    logger.info(s"Produced mercator_political.svg")

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

  }

}
