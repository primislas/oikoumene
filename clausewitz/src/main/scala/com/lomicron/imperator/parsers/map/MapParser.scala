package com.lomicron.imperator.parsers.map

import com.lomicron.eu4.model.map.MercatorMap
import com.lomicron.eu4.parsers.map.MapParser._
import com.lomicron.imperator.repository.api.{GeographicRepository, RepositoryFactory}
import com.lomicron.utils.collection.CollectionUtils.SeqEx
import com.typesafe.scalalogging.LazyLogging

object MapParser extends LazyLogging {
  private val ClausewitzMapParser = com.lomicron.eu4.parsers.map.MapParser

  def apply(repos: RepositoryFactory): GeographicRepository =
    MapParser.parseMap(repos)

  def parseMap(repos: RepositoryFactory): GeographicRepository = {
    val r = repos.resources
    val g = repos.geography

    logger.info("Parsing rivers...")
    val rivers = r.getRiversMap.map(ClausewitzMapParser.parseRivers).toSeq.flatten
    logger.info(s"Identified ${rivers.size} rivers")

    logger.info("Parsing map provinces...")
    val provs = r.getProvinceMap.map(gf => fetchMap(gf.path))
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


}
