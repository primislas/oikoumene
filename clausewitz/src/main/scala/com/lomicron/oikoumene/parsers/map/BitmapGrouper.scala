package com.lomicron.oikoumene.parsers.map

import java.awt.image.BufferedImage

import com.lomicron.oikoumene.parsers.map.MapParser.getRGB
import com.lomicron.utils.collection.CollectionUtils.OptionEx

import scala.collection.mutable

object BitmapGrouper {

  def toGroups(img: BufferedImage): Array[Array[Int]] = {
    val groups = Array.ofDim[Int](img.getWidth(), img.getHeight())

    def leftNeighborGroup(x: Int, y: Int): Option[Int] =
      if (x > 0 && y >= 0) Some(groups(x - 1)(y))
      else None

    def topNeighborGroup(x: Int, y: Int): Option[Int] =
      if (x >= 0 && y > 0) Some(groups(x)(y - 1))
      else None

    def leftNeighborColorMatches(x: Int, y: Int, c: Int): Boolean =
      getRGB(img, x - 1, y).contains(c)

    def topNeighborColorMatches(x: Int, y: Int, c: Int): Boolean =
      getRGB(img, x, y - 1).contains(c)

    var groupIds = 0
    def nextId(): Int = {
      groupIds = groupIds + 1
      groupIds
    }

    val mgr = GroupMgr()

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val color = getRGB(img, x, y).get

      val isLeftGroup = leftNeighborColorMatches(x, y, color)
      val isTopGroup = topNeighborColorMatches(x, y, color)

      def setGroup(id: Int): Unit =
        groups(x)(y) = id

      if (isLeftGroup) {
        if (isTopGroup) {
          val leftGroup = leftNeighborGroup(x, y)
          val topGroup = topNeighborGroup(x, y)
          if (!leftGroup.contentsEqual(topGroup))
            mgr.add(leftGroup.get, topGroup.get)
          topNeighborGroup(x, y).foreach(setGroup)
        } else
          leftNeighborGroup(x, y).foreach(setGroup)
      } else if (isTopGroup)
        topNeighborGroup(x, y).foreach(setGroup)
      else {
        val id = nextId()
        mgr.add(id)
        groups(x)(y) = id
      }
    }

    for (y <- 0 until img.getHeight; x <- 0 until img.getWidth) {
      val id = groups(x)(y)
      val gid = mgr.groupIdOf(id).getOrElse(id)
      if (gid != id) groups(x)(y) = gid
    }

    groups
  }

}

case class SameGroup() {
  private val ids = scala.collection.mutable.Set.empty[Int]
  private var minId = Integer.MAX_VALUE

  def id: Int = minId

  def add(a: Int): SameGroup = {
    ids.add(a)
    minId = a
    this
  }

  def add(a: Int, b: Int): SameGroup = {
    ids.add(a)
    ids.add(b)
    if (a < minId) minId = a
    if (b < minId) minId = b
    this
  }

  def add(grp: SameGroup): SameGroup = {
    grp.ids.foreach(ids.add)
    minId = Math.min(minId, grp.minId)
    this
  }

  def has(id: Int): Boolean = ids.contains(id)

  def getIds: mutable.Set[Int] = ids

}

case class GroupMgr() {
  private val ids = scala.collection.mutable.Map.empty[Int, SameGroup]

  def add(a: Int): Int = {
    add(a, a)
  }

  def add(a: Int, b: Int): Int = {
    // TODO most likely could be optimized considerably,
    // look here if willing to squeeze extra performance
    val exstA = ids.getOrElse(a, SameGroup().add(a))
    val exstB = ids.getOrElse(b, SameGroup().add(b))
    val merged = exstA.add(exstB)
    merged.getIds.foreach(ids.put(_, merged))
    exstA.id
  }

  def groupOf(a: Int): Option[SameGroup] = ids.get(a)

  def groupIdOf(a: Int): Option[Int] = ids.get(a).map(_.id)

}
