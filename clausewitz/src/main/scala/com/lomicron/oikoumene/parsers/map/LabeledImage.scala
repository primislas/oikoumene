package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.parsers.map.Tracer.Labels

import java.awt.image.BufferedImage

case class LabeledImage(img: BufferedImage, labels: Labels, regions: Seq[BitmapRegion]) {
  def size: Int = regions.size
}
