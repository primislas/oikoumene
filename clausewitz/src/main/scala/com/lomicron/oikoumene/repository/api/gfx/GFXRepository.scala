package com.lomicron.oikoumene.repository.api.gfx

import java.awt.image.BufferedImage

trait GFXRepository {

  def findFlag(tag: String): Option[BufferedImage]

  def findReligion(id: String): Option[BufferedImage]

  def findTradeGood(id: String): Option[BufferedImage]

}
