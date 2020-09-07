package com.lomicron.oikoumene.parsers.map

import java.awt.image.{BufferedImage, IndexColorModel}
import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.utils.geometry.Shape
import com.typesafe.scalalogging.LazyLogging
import javax.imageio.ImageIO
import com.lomicron.utils.collection.CollectionUtils.SeqEx

import scala.Function.tupled
import scala.util.Try

object MapParser extends LazyLogging {

  def apply(repos: RepositoryFactory): GeographicRepository =
    MapParser.parseMap(repos)

  def parseMap(repos: RepositoryFactory): GeographicRepository = {
    val r = repos.resources
    val g = repos.geography

    logger.info("Parsing rivers...")
    val rivers = r.getRiversMap.map(fetchMap)
      .map(parseRivers)
      .getOrElse(Seq.empty)
      .map(_.smooth)
    g.map.createRivers(rivers)
    logger.info(s"Identified ${rivers.size} rivers")

    logger.info("Parsing terrain...")
    val terrainMap = r.getTerrainMap.map(fetchMap)
    terrainMap
      .map(parseTerrainColors)
      .map(cs => cs.map(Color(_)))
      .foreach(colors => g.map.rebuildTerrainColors(colors))
    logger.info("Identified terrain colors...")

    logger.info("Parsing map provinces...")
    val provs = r.getProvinceMap.map(fetchMap)
    val tiles = for {
      provinces <- provs
      terrain <- r.getTerrainMap.map(fetchMap)
      height <- r.getHeightMap.map(fetchMap)
    } yield parseMapTiles(provinces, terrain, height)
    logger.info(s"Identified ${tiles.map(_.size).getOrElse(0)} map tiles")

    logger.info("Parsing map terrain...")
    val terrainByProv = tiles.getOrElse(Seq.empty)
      .mapBy(_.color)
      .mapValues(_.terrainColor)
      .filter(_._2.isDefined)
      .mapValues(_.get)
    g.map.setTerrainProvinceColors(terrainByProv)
    logger.info(s"Identified terrain of ${terrainByProv.size} provinces from terrain map")

    logger.info("Calculating map shapes...")
    val shapes = provs.map(parseProvinceShapes).getOrElse(Seq.empty).map(_.withPolygon)
    logger.info(s"Identified ${shapes.size} map shapes")
    val borders = shapes.flatMap(_.borders).distinct
    logger.info(s"Identified ${borders.size} border segments")
    val width = provs.map(_.getWidth).getOrElse(0)
    val height = provs.map(_.getHeight).getOrElse(0)
    val mercator = MercatorMap(shapes, borders, rivers, width, height)
    g.map.updateMercator(mercator)

    logger.info("Calculating map routes...")
    val provinceMap = r.getProvinceMap.map(fetchMap)
    provinceMap.map(parseRoutes).foreach(g.map.updateTileRoutes)
    logger.info("Identified map routes")

    g
  }

  /**
    * Returns a tuple of province colors and routes from the province bitmap.
    *
    * @param provinces - [[java.awt.image.BufferedImage BufferedImage]] representing a province
    * @return a tuple of province colors and routes connecting them
    */
  def parseMapTiles(provinces: BufferedImage,
                    terrain: BufferedImage,
                    height: BufferedImage)
  : Seq[Tile] = {

    val pixels = for (x <- 0 until provinces.getWidth;
                      y <- 0 until provinces.getHeight)
      yield Pixel(x, y, getRGB(provinces, x, y), getRGB(terrain, x, y), getRGB(height, x, y))

    pixels
      .filter(_.color.isDefined)
      .groupBy(_.color.get)
      .map(tupled { (c, pixels) => Tile(c, pixels) })
      .toSeq
  }

  def parseRoutes(img: BufferedImage): Seq[TileRoute] = {
    var routes = Set.empty[TileRoute]
    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth)
      routes = routes ++ parseRoutes(img, x, y)

    routes.toSeq
  }

  def parseRoutes(img: BufferedImage, x: Int, y: Int): Seq[TileRoute] = {
    val leftX = if (x > 0) x - 1 else img.getWidth - 1
    val left = getRoute(Option(img.getRGB(x, y)), Option(img.getRGB(leftX, y)))
    val top = if (y > 0) getRoute(Option(img.getRGB(x, y)), Option(img.getRGB(x, y - 1))) else None
    Seq(left, top).flatten
  }

  def parseTerrainColors(terrain: BufferedImage): Array[Int] = {
    val cm = terrain.getColorModel.asInstanceOf[IndexColorModel]
    val terrainColors = Array.fill(cm.getMapSize)(0)
    cm.getRGBs(terrainColors)
    terrainColors
  }

  def parseRivers(rivers: BufferedImage): Seq[River] = RiverParser.trace(rivers)

  def parseProvinceShapes(img: BufferedImage): Seq[Shape] = Tracer.trace(img)

  def getRGB(img: BufferedImage, x: Int, y: Int): Option[Int] =
    Try(img.getRGB(x, y)).toOption

  def getRoute(source: Option[Int], target: Option[Int]): Option[TileRoute] = for {
    s <- source
    t <- target
    routeOpt <- getRoute(s, t)
  } yield routeOpt

  def getRoute(source: Int, target: Int): Option[TileRoute] = {
    if (source != target) Some(TileRoute(source, target))
    else None
  }

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

}
