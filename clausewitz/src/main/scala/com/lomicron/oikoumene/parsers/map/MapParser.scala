package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.oikoumene.repository.api.resources.ResourceRepository
import com.lomicron.utils.collection.CollectionUtils.{MapEx, SeqEx}
import com.lomicron.utils.geometry.SchneidersFitter.fit
import com.lomicron.utils.geometry.TPath.Polypath
import com.lomicron.utils.geometry.{Border, Polygon, SchneidersFitter, Shape}
import com.typesafe.scalalogging.LazyLogging

import java.awt.image.{BufferedImage, IndexColorModel}
import java.nio.file.{Path, Paths}
import javax.imageio.ImageIO
import scala.Function.tupled
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParSeq
import scala.util.Try

object MapParser extends LazyLogging {

  def apply(repos: RepositoryFactory): GeographicRepository =
    MapParser.parseMap(repos)

  def parseMap(repos: RepositoryFactory): GeographicRepository = {
    val r = repos.resources
    val g = repos.geography

    logger.info("Parsing rivers...")
    val rivers = parseRivers(r, g)
    logger.info(s"Identified ${rivers.size} rivers")

    logger.info("Parsing terrain...")
    val terrainColors = parseTerrainColors(r, g)
    logger.info(s"Identified terrain ${terrainColors.length} colors")

    logger.info("Parsing map provinces...")
    val provs = r.getProvinceMap.map(fetchMap)
    val terrainByProv = parseProvinceTerrain(provs, r, g)
    logger.info(s"Identified terrain of ${terrainByProv.size} provinces from terrain map")

    logger.info("Calculating map shapes...")
    var shapes = provs.map(parseProvinceShapes).getOrElse(Seq.empty).map(_.withPolygon)
    logger.info(s"Identified ${shapes.size} map shapes")
    val allBorders = shapes.flatMap(_.borders)
    logger.info(s"Identified ${allBorders.size} border segments")
    // TODO: .distinct and .toSet produce different results; why? how? investigate
    val borders = allBorders.distinct.map(fitBorderCurves)
    val bconfigs = borders.toMapEx(b => (b, b))
    logger.info(s"Identified ${borders.size} unique border segments")
    shapes = shapes.map(fitProvinceCurves(_, bconfigs))
    logger.info(s"Calculated province curvature")

    val width = provs.map(_.getWidth).getOrElse(0)
    val height = provs.map(_.getHeight).getOrElse(0)
    val mercator = MercatorMap(shapes, borders, rivers, width, height)
    g.map.updateMercator(mercator)

    logger.info("Calculating map routes...")
    val routes = provs.map(parseRoutes).getOrElse(Seq.empty)
    g.map.updateTileRoutes(routes)
    logger.info(s"Identified ${routes.size} map routes")

    g
  }

  def parallelizeImage(img: BufferedImage): ParSeq[(Int, Int)] = {
    val parallelism = java.lang.Runtime.getRuntime.availableProcessors
    val step = img.getHeight / parallelism
    (0 until parallelism)
      .par
      .map(i => (i * step, (i + 1) * step))
      .map(t => if (t._2 > img.getHeight) (t._1, img.getHeight) else t)
  }

  def parseRivers(r: ResourceRepository, g: GeographicRepository): Seq[River] = {
    val rivers = r.getRiversMap.map(fetchMap)
      .map(parseRivers)
      .getOrElse(Seq.empty)
      .map(_.smooth)
      .map(fitRiverCurves)
    g.map.createRivers(rivers)
    rivers
  }

  def parseTerrainColors(r: ResourceRepository, g: GeographicRepository): Array[Color] = {
    val terrainMap = r.getTerrainMap.map(fetchMap)
    val colors = terrainMap.map(parseTerrainColors).map(cs => cs.map(Color(_)))
    colors.foreach(colors => g.map.rebuildTerrainColors(colors))
    colors.getOrElse(Array.empty)
  }

  def parseProvinceTerrain
  (
    provinces: Option[BufferedImage],
    r : ResourceRepository,
    g: GeographicRepository
  ): Map[Color, Color] = {
    val terrainByProvOpt = for {
      provs <- provinces
      terrain <- r.getTerrainMap.map(fetchMap)
    } yield parseMapProvinceTerrain(provs, terrain)
    val terrainByProv = terrainByProvOpt.getOrElse(Map.empty)

    g.map.setTerrainProvinceColors(terrainByProv)
    terrainByProv
  }

  def parseMapProvinceTerrain
  (
    provinces: BufferedImage,
    terrain: BufferedImage
  ): Map[Color, Color] = {
    parallelizeImage(provinces)
      .map(parseMapProvinceTerrain(provinces, terrain, _))
      .reduce((m1, m2) => {
        m2.foreach(e => {
          val (pColor, pTerrain2) = e
          val updatedTerrain = m1
            .get(pColor)
            .map(pTerrain1 => {
              pTerrain2
                .foreach(e => {
                  val (tColor2, tColorCount2) = e
                  val tColorCount = pTerrain1.getOrElse(tColor2, 0) + tColorCount2
                  pTerrain1 += (tColor2 -> tColorCount)
                })
              pTerrain1
            })
            .getOrElse(pTerrain2)
          m1 += (pColor -> updatedTerrain)
        })
        m1
      })
      .toMap
      .mapValuesEx(pColors => Color(pColors.maxBy(_._2)._2))
      .mapKeys(Color(_))
  }

  def parseMapProvinceTerrain
  (
    provinces: BufferedImage,
    terrain: BufferedImage,
    yRange: (Int, Int)
  ): collection.mutable.Map[Int, collection.mutable.Map[Int, Int]] = {
    val terrainColorsByProv: collection.mutable.Map[Int, collection.mutable.Map[Int, Int]] = collection.mutable.Map.empty
    for {
      x <- 0 until provinces.getWidth
      y <- yRange._1 until yRange._2
    } {
      for {
        provColor <- getRGB(provinces, x, y)
        terrainColor <- getRGB(terrain, x, y)
      } {
        val provTerrain = terrainColorsByProv.getOrElse(provColor, collection.mutable.Map.empty)
        val tCount = provTerrain.getOrElse(terrainColor, 0) + 1
        provTerrain += (terrainColor -> tCount)
        terrainColorsByProv += (provColor -> provTerrain)
      }
    }
    terrainColorsByProv
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

  def parseRoutes(provinces: BufferedImage): Seq[TileRoute] = {
    parallelizeImage(provinces)
      .map(range => {
        val routesByProv = collection.mutable.Map.empty[Int, Set[Int]]
        for {
          y <- range._1 until range._2
          x <- 0 until provinces.getWidth
          routes <- parseRoutes(provinces, x, y)
        } {
          if (routes._2.nonEmpty) {
            val from = routes._1
            if (routesByProv.contains(from))
              routesByProv.get(from).foreach(exstTo => routesByProv += from -> (exstTo ++ routes._2))
            else
              routesByProv += from -> routes._2.toSet
          }
        }
        routesByProv
      })
      .reduce((rs1, rs2) => {
        rs2.foreach(e => {
          val (from2, to2) = e
          if (rs1.contains(from2))
            rs1.get(from2).foreach(to1 => rs1 += from2 -> (to1 ++ to2))
          else
            rs1 += e
        })
        rs1
      })
      .flatMap(e => {
        val (from, to) = e
        to.map(t => TileRoute(from, t))
      })
      .toSet
      .toSeq
  }

  def parseRoutes(img: BufferedImage, x: Int, y: Int): Option[(Int, Seq[Int])] = {
    val leftX = if (x > 0) x - 1 else img.getWidth - 1
    val pColor = Option(img.getRGB(x, y))
    val left = getRoute(pColor, Option(img.getRGB(leftX, y)))
    val top = if (y > 0) getRoute(pColor, Option(img.getRGB(x, y - 1))) else None
    pColor.map(c => c -> Seq(left, top).flatten)
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

  def getRoute(source: Option[Int], target: Option[Int]): Option[Int] = for {
    s <- source
    t <- target
    routeOpt <- getRoute(s, t)
  } yield routeOpt

  def getRoute(source: Int, target: Int): Option[Int] = {
    if (source != target) Some(target)
    else None
  }

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

  def fitBorderCurves(b: Border): Border = {
    val path = SchneidersFitter.fit(b.points)
    b.withPath(path)
  }

  def fitRiverCurves(r: River): River = {
    val segs = r.path.map(seg => seg.withPath(SchneidersFitter.fit(seg.points)))
    r.copy(path = segs)
  }

  def fitProvinceCurves(p: Shape, bconfigs: Map[Border, Border]): Shape = {
    val path = p.borders.flatMap(getBorderPath(_, bconfigs))
    val clipPaths = p.clip.map(p => p.withPath(getPolygonPath(p, bconfigs))).filter(_.path.nonEmpty)
    p.copy(path = path, clip = clipPaths)
  }

  def getBorderPath(b: Border, bconfigs: Map[Border, Border]): Polypath = {
    val confPath = bconfigs.get(b).map(_.path).getOrElse(fit(b.points))
    if (confPath.isEmpty) Seq.empty
    else if (confPath.head.points.head == b.points.head) confPath
    else confPath.map(_.reverse).reverse
  }

  def getPolygonPath(p: Polygon, bconfigs: Map[Border, Border]): Polypath =
    getBorderPath(Border(p.points :+ p.points.head), bconfigs)

}
