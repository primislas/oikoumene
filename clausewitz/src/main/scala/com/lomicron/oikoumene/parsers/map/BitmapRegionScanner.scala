package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.parsers.map.BitmapRegionScanner.RegionStarts
import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
import com.lomicron.oikoumene.parsers.map.Tracer.Labels
import com.typesafe.scalalogging.LazyLogging

import java.awt.image.BufferedImage
import scala.collection.mutable


object BitmapRegionScanner extends LazyLogging {
  type RegionStarts = collection.mutable.Map[Int, BitmapRegion]

  def labelRegions(img: BufferedImage): LabeledImage = {
    val labels = Array.ofDim[Int](img.getWidth(), img.getHeight())
    val regionStarts = collection.mutable.Map.empty[Int, BitmapRegion]
    val scanner = BitmapRegionScanner(img, labels, regionStarts)

    // labeling first pass:
    //    * assigning labels row by row
    //    * marking labels belonging to same regions
    val mgr = MapParser
      .parallelizeImage(img)
      .map(fromTo => scanner.label(fromTo._1, fromTo._2))
      .reduce(_ add _)

    MapParser
      .parallelizeImage(img)
      .map(_._1)
      .drop(1)
      .foreach(y => {
        for (x <- 0 until img.getWidth)
          scanner.mergeRegionsVertically(x, y, mgr)
      })
    logger.info("Labeling first pass: Done")

    // merging labels belong to the same region
    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val id = labels(x)(y)
      val gid = mgr.regionLabelOf(id).getOrElse(id)
      if (gid != id) labels(x)(y) = gid
    }

    val regionIds = mgr.regionLabels
    logger.info(s"Labeling 2nd pass: ${regionIds.size} regions")
    val regions = regionStarts.view.filterKeys(regionIds.contains).values.toList
    LabeledImage(img, labels, regions)
  }

}

case class BitmapRegionScanner(img: BufferedImage, labels: Labels, regionStarts: RegionStarts) extends LazyLogging {

  def leftNeighborLabel(x: Int, y: Int): Option[Int] =
    if (x > 0 && y >= 0) Some(labels(x - 1)(y))
    else None

  def topNeighborLabel(x: Int, y: Int, yFrom: Int = 0): Option[Int] =
    if (x >= 0 && y > yFrom) Some(labels(x)(y - 1))
    else None

  def leftNeighborColorMatches(x: Int, y: Int, c: Int): Boolean =
    getRGB(img, x - 1, y).contains(c)

  def topNeighborColorMatches(x: Int, y: Int, c: Int, yFrom: Int = 0): Boolean =
    y > yFrom && getRGB(img, x, y - 1).contains(c)

  def label(yFrom: Int, yUntil: Int): RegionMgr = {
    var labelSeq = img.getWidth * yFrom

    def nextId(): Int = {
      labelSeq = labelSeq + 1
      labelSeq
    }

    val mgr = RegionMgr()

    for (y <- yFrom until yUntil; x <- 0 until img.getWidth) {
      if (!mergeRegionsIfMatch(x, y, mgr, yFrom)) {
        val id = nextId()
        mgr.add(id)
        labels(x)(y) = id
        regionStarts.put(id, BitmapRegion(id, x, y))
      }
    }

    mgr
  }

  def mergeRegionsIfMatch(x: Int, y: Int, mgr: RegionMgr, yFrom: Int = 0): Boolean = {
    val color = getRGB(img, x, y).get

    val isLeftRegion = leftNeighborColorMatches(x, y, color)
    val isTopRegion = topNeighborColorMatches(x, y, color, yFrom)

    def setLabel(id: Int): Unit =
      labels(x)(y) = id

    if (isLeftRegion) {
      if (isTopRegion) {
        for {
          leftLabel <- leftNeighborLabel(x, y)
          topLabel <- topNeighborLabel(x, y)
        } {
          if (leftLabel != topLabel)
            mgr.add(leftLabel, topLabel)
          val minLabel = Math.min(leftLabel, topLabel)
          setLabel(minLabel)
        }
      } else
        leftNeighborLabel(x, y).foreach(setLabel)
    } else if (isTopRegion)
      topNeighborLabel(x, y).foreach(setLabel)

    isLeftRegion || isTopRegion
  }

  def mergeRegionsVertically(x: Int, y: Int, mgr: RegionMgr): Boolean = {
    val color = getRGB(img, x, y).get
    val isTopRegion = topNeighborColorMatches(x, y, color)

    if (isTopRegion) {
      topNeighborLabel(x, y)
        .foreach(topLabel => {
          val label = labels(x)(y)
          if (topLabel != label)
            mgr.add(topLabel, label)
        })
    }

    isTopRegion
  }

}

case class SameRegion() {
  private val labels = scala.collection.mutable.Set.empty[Int]
  private var minLabel = Integer.MAX_VALUE

  def id: Int = minLabel

  def add(a: Int): SameRegion = {
    labels.add(a)
    minLabel = a
    this
  }

  def add(a: Int, b: Int): SameRegion = {
    labels.add(a)
    labels.add(b)
    if (a < minLabel) minLabel = a
    if (b < minLabel) minLabel = b
    this
  }

  def add(grp: SameRegion): SameRegion = {
    grp.labels.foreach(labels.add)
    minLabel = Math.min(minLabel, grp.minLabel)
    this
  }

  def has(id: Int): Boolean = labels.contains(id)

  def getLabels: mutable.Set[Int] = labels

  def size: Int = labels.size

}

case class RegionMgr() {
  private val labels = scala.collection.mutable.Map.empty[Int, SameRegion]

  def add(a: Int): Int = {
    labels.put(a, SameRegion().add(a))
    a
  }

  def add(a: Int, b: Int): Int = {
    val merged =
      if (labels.contains(a)) {
        val agrp = labels(a)
        if (labels.contains(b)) {
          val bgrp = labels(b)
          if (agrp.size >= bgrp.size) {
            bgrp.getLabels.foreach(labels.put(_, agrp))
            agrp.add(bgrp)
          } else {
            agrp.getLabels.foreach(labels.put(_, bgrp))
            bgrp.add(agrp)
          }
        } else {
          labels.put(b, agrp)
          agrp.add(b)
        }
      } else {
        if (labels.contains(b)) {
          val bgrp = labels(b)
          labels.put(a, bgrp)
          bgrp.add(a)
        } else {
          val grp = SameRegion().add(a, b)
          labels.put(a, grp)
          labels.put(b, grp)
          grp
        }
      }

    merged.id
  }

  def add(mgr: RegionMgr): RegionMgr = {
    labels ++= mgr.labels
    this
  }

  def groupOf(a: Int): Option[SameRegion] = labels.get(a)

  def regionLabelOf(a: Int): Option[Int] = labels.get(a).map(_.id)

  def regionLabels: Set[Int] = labels.values.map(_.id).toSet

}
