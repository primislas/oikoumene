package com.lomicron.oikoumene.io

import java.awt.image.BufferedImage
import java.io.File
import java.io.FileReader
import java.io.Reader
import javax.imageio.ImageIO

import scala.util.Try
import scala.util.Success
import scala.util.Failure

object FileIO {
  
  def cleanly[A,B](resource: A)(cleanup: A => Unit)(doWork: A => B): Try[B] = {
    try {
      Success(doWork(resource))
    } catch {
      case e: Exception => Failure(e)
    } finally {
      try {
        if (resource != null) cleanup(resource)
      } catch {
        case e: Exception => println(e)
      }
    }
  }
  
  def readMap(path: String): Array[Array[Int]] = {
    val bitmap = ImageIO.read(new File(path))
    val height = bitmap.getHeight
    val width = bitmap.getWidth
    val bitmapLines = for {
      y <- 0 until height
    } yield bitmap.getSubimage(0, y, width, 1)
    bitmapLines.map(bitmapLineToRgb).map(_.toArray).toArray
  }

  private def bitmapLineToRgb(bitmap: BufferedImage) =
    for { y <- 0 until bitmap.getWidth } yield bitmap.getRGB(0, y)
  
  def readConfig(path: String): Reader = {
    new FileReader(path)
  }
}