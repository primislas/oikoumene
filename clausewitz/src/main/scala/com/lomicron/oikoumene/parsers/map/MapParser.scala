package com.lomicron.oikoumene.parsers.map

import java.awt.image.{BufferedImage, IndexColorModel}
import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.{Pixel, Route, Tile}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import javax.imageio.ImageIO

import scala.Function.tupled
import scala.util.Try

object MapParser {

  def apply(repos: RepositoryFactory): GeographicRepository =
    MapParser.parseMap(repos)

  def parseMap(repos: RepositoryFactory): GeographicRepository = {
    val r = repos.resources
    val map = for {
      provinces <- r.getProvinceMap.map(fetchMap)
      terrain <- r.getTerrainMap.map(fetchMap)
      height <- r.getHeightMap.map(fetchMap)
    } yield parseMap(provinces, terrain, height)

    val g = repos.geography
    map.foreach(m => {
      g.map.create(m.tiles)
      g.map.rebuildTerrainColors(m.terrainColors.map(Color(_)))
    })

    g
  }

  /**
    * Returns a tuple of province colors and routes from the province bitmap.
    *
    * @param provinces - [[java.awt.image.BufferedImage BufferedImage]] representing a province
    * @return a tuple of province colors and routes connecting them
    */
  def parseMap(provinces: BufferedImage,
               terrain: BufferedImage,
               height: BufferedImage)
  : ParsedMap = {

    val cm = terrain.getColorModel.asInstanceOf[IndexColorModel]
    val terrainColors = Array.fill(cm.getMapSize)(0)
    cm.getRGBs(terrainColors)

    val pixels = for (x <- 0 until provinces.getWidth;
                      y <- 0 until provinces.getHeight)
      yield Pixel(x, y, getRGB(provinces, x, y), getRGB(terrain, x, y), getRGB(height, x, y))

    val routes = pixels
      .filter(p => p.x != 0 && p.y != 0)
      .flatMap(p => getRoute(p.color, Option(provinces.getRGB(p.x - 1, p.y))).toSeq ++ getRoute(p.color, Option(provinces.getRGB(p.x, p.y - 1))).toSeq)
      .distinct

    val tiles = pixels
      .filter(_.color.isDefined)
      .groupBy(_.color.get)
      .map(tupled { (c, pixels) => Tile(c, pixels) })
      .toSeq

    ParsedMap(tiles, routes, terrainColors)
  }

  def getRGB(img: BufferedImage, x: Int, y: Int): Option[Int] =
    Try(img.getRGB(x, y)).toOption

  def getRoute(source: Option[Int], target: Option[Int]): Option[Route] = for {
    s <- source
    t <- target
    routeOpt <- getRoute(s, t)
  } yield routeOpt

  def getRoute(source: Int, target: Int): Option[Route] = {
    if (source != target) Some(Route(source, target))
    else None
  }

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

}
