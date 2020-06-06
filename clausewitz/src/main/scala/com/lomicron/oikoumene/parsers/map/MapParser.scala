package com.lomicron.oikoumene.parsers.map

import java.awt.Point
import java.awt.image.{BufferedImage, IndexColorModel}
import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.{Pixel, TileRoute, Tile}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.{GeographicRepository, MapRepository}
import javax.imageio.ImageIO

import scala.Function.tupled
import scala.math.BigDecimal.RoundingMode
import scala.util.Try

object MapParser {

  def apply(repos: RepositoryFactory): GeographicRepository =
    MapParser.parseMap(repos)

  def parseMap(repos: RepositoryFactory): GeographicRepository = {
    val r = repos.resources
    val g = repos.geography

    val terrainMap = r.getTerrainMap.map(fetchMap)
    terrainMap
      .map(parseTerrainColors)
      .map(cs => cs.map(Color(_)))
      .foreach(colors => g.map.rebuildTerrainColors(colors))

    val provinceMap = r.getProvinceMap.map(fetchMap)
    provinceMap.map(parseRoutes).foreach(g.map.updateTileRoutes)

    //    map.foreach(m => {
    //      g.map.create(m.tiles)
    //      g.map.rebuildTerrainColors(m.terrainColors.map(Color(_)))
    //    })

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

  def parseWorldMap(img: BufferedImage, mapRepo: MapRepository): MapRepository = {
    val (polygons, sphere) = parseWorldMap(img)
    mapRepo.updateMercator(polygons).updateSphericalMap(sphere)
  }

  def parseWorldMap(img: BufferedImage): (Seq[Polygon], SphericalMap) = {
    val polygons = Tracer.trace(img)

    val circumference = if (img.getWidth > img.getHeight) img.getWidth else img.getHeight
    val radius = BigDecimal(circumference) / (2 * Math.PI)
    val radiusInt = radius.setScale(0, RoundingMode.CEILING).toInt
    val center = new Point(radiusInt, radiusInt)
    val offset = new Point(radiusInt - img.getWidth / 2, radiusInt - img.getHeight / 2)
    val sphere = Geometry.toSpherical(polygons, center, radius.toDouble, offset)

    (polygons, sphere)
  }

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
