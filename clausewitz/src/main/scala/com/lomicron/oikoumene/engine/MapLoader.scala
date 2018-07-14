package com.lomicron.oikoumene.engine

import java.awt.image.BufferedImage
import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

import com.lomicron.oikoumene.model.map.{Route, Tile}

import scala.util.Try

object MapLoader {
  type Provinces = (Seq[Tile], Seq[Route])

  def loadMap(path: String): Try[Provinces] = loadMap(Paths.get(path))

  def loadMap(path: Path): Try[Provinces] = Try(unsafeLoadMap(path))

  private def unsafeLoadMap(path: Path) = {
    val provinces = parseBitmap(fetchMap(path))
    val tiles = provinces._1.map(Tile).toSeq
    Tuple2(tiles, Nil)
  }

  /**
   * Returns a tuple of province colors and routes from the province bitmap.
   *
   * @param map - [[java.awt.image.BufferedImage BufferedImage]] representing a province
   * @return a tuple of province colors and routes connecting them
   */
  def parseBitmap(map: BufferedImage): (Set[Int], Set[Route]) = {
    var colors = Set[Int]()
    var routes = Set[Route]()
    for (x <- 0 until map.getWidth; y <- 0 until map.getHeight) {
      val source = map.getRGB(x, y)
      colors += source
      if (x != 0 && y != 0) {
        getRoute(source, map.getRGB(x - 1, y - 1)) foreach { route => routes += route }
      }
    }

    (colors, routes)
  }

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