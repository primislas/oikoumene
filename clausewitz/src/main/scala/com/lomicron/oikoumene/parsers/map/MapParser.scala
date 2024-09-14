package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map._
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.{GeographicRepository, MapRepository}
import com.lomicron.oikoumene.repository.api.resources.{GameFile, ResourceRepository}
import com.lomicron.utils.collection.CollectionUtils.{MapEx, SeqEx}
import com.lomicron.utils.geometry.SchneidersFitter.fit
import com.lomicron.utils.geometry.TPath.Polypath
import com.lomicron.utils.geometry.{Border, Polygon, SchneidersFitter, Shape}
import com.typesafe.scalalogging.LazyLogging

import java.awt.{Image, RenderingHints}
import java.awt.image.{BufferedImage, IndexColorModel}
import java.nio.file.{Files, Path, Paths}
import javax.imageio.ImageIO
import scala.Function.tupled
import scala.collection.parallel.CollectionConverters._
import scala.collection.parallel.immutable.ParSeq
import scala.util.Try

object MapParser extends LazyLogging {

  private val fittingError = 1.5
  private val fittingScale = 20.0

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
    val treeTerrainColors = parseTreeTerrainColors(r, g)
    logger.info(s"Identified ${terrainColors.length} terrain colors, ${treeTerrainColors.length} tree terrain colors")

    logger.info("Parsing map provinces...")
    val provs = r.getProvinceMap.map(gf => fetchMap(gf.path))
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
    val parStep = img.getHeight / parallelism
    val effectiveParallelism = if (parStep <= 512) 1 else parallelism
    val step = if (parStep <= 512) img.getHeight else parStep
    (0 until effectiveParallelism)
      .par
      .map(i => (i * step, (i + 1) * step))
      .map(t => if (t._2 > img.getHeight) (t._1, img.getHeight) else t)
  }

  def parseRivers(r: ResourceRepository, g: GeographicRepository): Seq[River] = {
    val rivers = r.getRiversMap
      .map(gf => fetchMap(gf.path))
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

  def parseTreeTerrainColors(r: ResourceRepository, g: GeographicRepository): Array[Color] = {
    val treeMap = r.getTreeMap.map(fetchMap)
    val colors = treeMap.map(parseTerrainColors).map(cs => cs.map(Color(_)))
    colors.foreach(colors => g.map.rebuildTreeTerrainColors(colors))
    colors.getOrElse(Array.empty)
  }

  def parseProvinceTerrain
  (
    provinces: Option[BufferedImage],
    r : ResourceRepository,
    g: GeographicRepository
  ): Map[Color, String] = {
    val provTerrains = for {
      provs <- provinces
      terrain <- r.getTerrainMap.map(fetchMap)
      trees <- r.getTreeMap.map(fetchMap)
      rivers <- r.getRiversMap.map(fetchMap)
    } yield parseMapProvinceTerrain(g.map, provs, terrain, trees, rivers)

    Seq(Color(31,161,79))
      .foreach(color => {
        val iColor = color.toInt
        val provPixels = provTerrains.getOrElse(Seq.empty).filter(_.provColor == iColor)
        val reduced = provPixels.reduce(_ + _)
        println(f"$color -> $reduced")
      })

    val terrainTypeByProvColor = provTerrains.getOrElse(Seq.empty)
      .groupBy(_.provColor)
      .mapKeys(Color(_))
      .mapValuesEx(_.reduce(_ + _))
      .flatMapValues(_.terrainType)
    g.map.setProvinceTerrainTypes(terrainTypeByProvColor)

    terrainTypeByProvColor
  }

  def parseMapProvinceTerrain
  (
    mapRepo: MapRepository,
    provinces: BufferedImage,
    terrain: BufferedImage,
    trees: BufferedImage,
    rivers: BufferedImage,
  ): Seq[ProvTerrain] = {
    parallelizeImage(provinces)
      .flatMap(parseMapProvinceTerrain(mapRepo, provinces, terrain, trees, rivers, _))
      .toList
  }

  def parseMapProvinceTerrain
  (
    mapRepo: MapRepository,
    provinces: BufferedImage,
    terrain: BufferedImage,
    trees: BufferedImage,
    rivers: BufferedImage,
    yRange: (Int, Int)
  ): Seq[ProvTerrain] = {
//    val scaledTreeImg = trees.getScaledInstance(provinces.getWidth, provinces.getHeight, Image.SCALE_DEFAULT)

    // Create a buffered image with transparency
    val (w, h) = (provinces.getWidth, provinces.getHeight)
    val scaledTrees = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB)

    // Draw the image on to the buffered image
    val bGr = scaledTrees.createGraphics()
    bGr.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    bGr.drawImage(trees, 0, 0, w, h, null)
    bGr.dispose()
    ImageIO.write(scaledTrees, "bmp", Paths.get("upscaled_trees.bmp").toFile)

    val treeXFactor = trees.getWidth.toDouble / terrain.getWidth
    val treeYFactor = trees.getHeight.toDouble / terrain.getHeight
    val excludedRiverColors = Set(Color(0, 200, 255), Color(0, 100, 255), Color(0, 0, 200), Color(0, 150, 255))
      .map(_.toInt)

    val provTerrains = for {
      x <- 0 until provinces.getWidth
      y <- yRange._1 until yRange._2
    } yield {
      for {
        provColor <- getRGB(provinces, x, y)
        terrainColor <- getRGB(terrain, x, y)
//        treeColor <- getRGB(trees, (treeXFactor * x).toInt, (treeYFactor * y).toInt)
        treeColor <- getRGB(scaledTrees,x, y)
//        riverColor <- getRGB(rivers, x, y) if !excludedRiverColors.contains(riverColor)
      } yield {
        val terrainTypes = mapRepo
          .treeTerrainTypeOfTreeColor(treeColor)
          .orElse(mapRepo.terrainTypeOfTerrainColor(terrainColor))
          .map(_ -> 1)
          .toMap
        ProvTerrain(provColor, terrainTypes)
      }
    }
    provTerrains.flatten
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

  def fetchMap(gf: GameFile): BufferedImage =
    fetchMap(gf.path)

  def fetchMap(path: String): BufferedImage =
    fetchMap(Paths.get(path))

  def fetchMap(path: Path): BufferedImage =
    ImageIO.read(path.toFile)

  def fitBorderCurves(b: Border): Border = {
    val path = SchneidersFitter.fit(b.points, fittingError * fittingScale)
    b.withPath(path)
  }

  def fitRiverCurves(r: River): River = {
    val segs = r.path
      .map(seg => seg.copy(points = seg.points.map(_ * 5.0)))
      .map(seg => seg.withPath(SchneidersFitter.fit(seg.points, fittingError * fittingScale)))
      .filter(seg => {
        if (seg.path.exists(path => path.points.exists(p => p.x.isNaN || p.y.isNaN)))
          false
        else
          true
      })
    r.copy(path = segs)
  }

  def fitProvinceCurves(p: Shape, bconfigs: Map[Border, Border]): Shape = {
    val path = p.borders.flatMap(getBorderPath(_, bconfigs))
    val clipPaths = p.clip.map(p => p.withPath(getPolygonPath(p, bconfigs))).filter(_.path.nonEmpty)
    p.copy(path = path, clip = clipPaths)
  }

  def getBorderPath(b: Border, bconfigs: Map[Border, Border]): Polypath = {
    val confPath = bconfigs.get(b).map(_.path).getOrElse(fit(b.points, fittingError * fittingScale))
    if (confPath.isEmpty) Seq.empty
    else if (confPath.head.points.head == b.points.head) confPath
    else confPath.map(_.reverse).reverse
  }

  def getPolygonPath(p: Polygon, bconfigs: Map[Border, Border]): Polypath =
    getBorderPath(Border(p.points :+ p.points.head), bconfigs)

}

case class ProvTerrain(provColor: Int, terrain: Map[String, Int] = Map.empty, trees: Map[String, Int] = Map.empty) {
  def +(other: ProvTerrain): ProvTerrain = {
    val mergedTerrain = addMaps(terrain, other.terrain)
    val mergedTrees = addMaps(trees, other.trees)
    ProvTerrain(provColor, mergedTerrain, mergedTrees)
  }

  def terrainType: Option[String] =
    (trees.toSeq ++ terrain.toSeq)
      .sortBy(_._2)
      .lastOption
      .map(_._1)

  private def addMaps(m1: Map[String, Int], m2: Map[String, Int]): Map[String, Int] =
    (m1.keySet ++ m2.keySet)
      .map(terrainType => {
        val count1 = m1.getOrElse(terrainType, 0)
        val count2 = m2.getOrElse(terrainType, 0)
        (terrainType, count1 + count2)
      })
      .toMap
}