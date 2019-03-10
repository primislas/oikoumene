package com.lomicron.oikoumene.engine

import java.awt.image.BufferedImage
import java.nio.file.{Path, Paths}

import com.lomicron.oikoumene.model.map.{Pixel, Route, Tile}
import javax.imageio.ImageIO

import scala.Function.tupled
import scala.util.Try

object MapLoader {

  val provincesFile = "provinces.bmp"
  val terrainFile = "terrain.bmp"
  val heightFile = "height.bmp"

  type Provinces = (Seq[Tile], Seq[Route])

  def loadMap(path: String): Try[Provinces] = loadMap(Paths.get(path))

  def loadMap(path: Path): Try[Provinces] = Try(unsafeLoadMap(path))

  private def unsafeLoadMap(path: Path) = {
    val provinces = fetchMap(path.resolve(provincesFile))
    val terrain = fetchMap(path.resolve(terrainFile))
    val height = fetchMap(path.resolve(heightFile))

    parseMap(provinces, terrain, height)
    //    val tiles = provinces._1.map(Tile(_)).toSeq
    //    Tuple2(tiles, Nil)
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
  : (Seq[Tile], Seq[Route]) = {

    //    var colors = Set[Int]()
    //    var routes = Set[Route]()
    //
    val pixels = for (x <- 0 until provinces.getWidth;
                      y <- 0 until provinces.getHeight)
      yield Pixel(x, y, getRGB(provinces, x, y), getRGB(terrain, x, y), getRGB(height, x, y))

    val colors = pixels.flatMap(_.color).toSet
    val routes = pixels
      .filter(p => p.x != 0 && p.y != 0)
      // TODO x - 1, y AND x, y - 1 to cover all cases
      .flatMap(p => getRoute(p.color, Some(provinces.getRGB(p.x - 1, p.y - 1))))
      .distinct

    val tiles = pixels
      .filter(_.color.isDefined)
      .groupBy(_.color.get)
      .map(tupled { (c, pixels) => Tile(c) })
      .toSeq

    (tiles, routes)
  }

  private def getRGB(img: BufferedImage, x: Int, y: Int) = Try(img.getRGB(x, y)).toOption

  def getBitmapColors(bitmap: Array[Array[Int]]): Set[Int] = {
    bitmap.flatten.toSet[Int]
  }

  def getBitmapRoutes(bitmap: Array[Array[Int]]): Set[Route] = {
    //    for {
    //      x <- 0 to bitmap.size - 1
    //      y <- 0 to bitmap(x).size - 1
    //   } yield Set[Route]()
    Set.empty[Route]
  }

  def getRoute(source: Option[Int], target: Option[Int]): Option[Route] = for {
      s <- source
      t <- target
      routeOpt <- getRoute(s, t)
    } yield routeOpt

  def getRoute(source: Int, target: Int): Option[Route] = {
    if (source != target) Some(Route(source, target))
    else None
  }

  def getRoutes(bitmap: Array[Array[Int]], x: Int, y: Int): Set[Route] = {
    //    var routes = Set[Route]()
    //    val width = bitmap(0).size
    //    val height = bitmap.size
    //    //if (x > 0 && y > 0)
    //     routes
    Set[Route]()
  }

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

  def fetchTileConfig(path: String): Map[Int, Tile] = {

    ???
  }

}
