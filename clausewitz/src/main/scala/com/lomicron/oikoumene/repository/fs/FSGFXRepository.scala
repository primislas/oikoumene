package com.lomicron.oikoumene.repository.fs

import java.awt.image.BufferedImage
import java.io.File

import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.gfx.GFXRepository
import javax.imageio.ImageIO

// TODO account for mods
case class FSGFXRepository(repos: RepositoryFactory)
  extends GFXRepository {

  private val gameDir = repos.settings.gameDir.get
  private val tradeGoodsFile = s"$gameDir/gfx/interface/resources.dds"
  private val religionsFile = s"$gameDir/gfx/interface/icon_religion.dds"

  override def findFlag(tag: String): Option[BufferedImage] = {
    Option(tag)
      .map(t => s"$gameDir/gfx/flags/$t.tga")
      .flatMap(readImage)
  }

  override def findReligion(id: String): Option[BufferedImage] = {
    val index = repos.religions.find(id).toOption.map(_.icon - 1)
    val image = readImage(religionsFile)
    subImageAtIndex(image, index)
  }

  override def findTradeGood(id: String): Option[BufferedImage] = {
    val index = repos.tradeGoods.find(id).toOption.flatMap(_.index)
    val image = readImage(tradeGoodsFile)
    subImageAtIndex(image, index)
  }

  private def readImage(path: String): Option[BufferedImage] =
    Option(path).map(new File(_)).filter(_.exists()).map(ImageIO.read).flatMap(Option(_))

  private def subImageAtIndex(image: Option[BufferedImage], index: Option[Int]): Option[BufferedImage] =
    for {
      img <- image
      ind <- index
    } yield subImageAtIndex(img, ind)

  private def subImageAtIndex(image: BufferedImage, index: Int): BufferedImage = {
    val height = image.getHeight
    image.getSubimage(height * index, 0, height, height)
  }

}
