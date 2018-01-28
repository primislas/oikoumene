package com.lomicron.oikoumene.engine

import java.awt.image.BufferedImage
import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO

import com.lomicron.oikoumene.model.map.{Route, Tile}

import scala.util.Try

object MapLoader {
  type Provinces = Tuple2[Seq[Tile], Seq[Route]]

  def loadMap(path: String): Try[Provinces] = loadMap(Paths.get(path))

  def loadMap(path: Path): Try[Provinces] = Try(unsafeLoadMap(path))

  private def unsafeLoadMap(path: Path) = {
    val provinces = parseBitmap(fetchMap(path))
    val tiles = provinces._1.map(new Tile(_)).toSeq
    new Tuple2(tiles, Nil)
  }

  /**
   * Returns a tuple of province colors and routes from the province bitmap.
   *
   * @param map - {@link BufferedImage} representing a province 
   * @return
   */
  def parseBitmap(map: BufferedImage): Tuple2[Set[Int], Set[Route]] = {
    var colors = Set[Int]()
    var routes = Set[Route]()
    val maxX = map.getWidth - 1
    val maxY = map.getHeight - 1
    for (x <- 0 to maxX; y <- 0 to maxY) {
      val source = map.getRGB(x, y)
      colors += source
      if (x != 0 && y != 0) {
        getRoute(source, map.getRGB(x - 1, y - 1)) foreach { route => routes += route }
      }
    }
    //println("Loaded colors: " + colors)
    // generates a collection of pixel values - bitmap colors represented as int values
    //val pixels = for {
    //  x <- 0 to maxX - 1
    //  y <- 0 to maxY - 1
    //} yield map.getRGB(x, y)

    //val colors2 = pixels map (x => Set[Int]() + x) reduce (_ ++ _)
    //val colors3 = pixels.toSet[Int]

    new Tuple2(colors, routes)
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
    if (source != target) Some(new Route(source, target))
    else None
  }

  def getRoutes(bitmap: Array[Array[Int]], x: Int, y: Int): Set[Route] = {
    var routes = Set[Route]()
    val width = bitmap(0).size
    val height = bitmap.size
    //if (x > 0 && y > 0)
     routes
  }

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

  def fetchTileConfig(path: String): Map[Int, Tile] = {

    ???
  }

}