package com.lomicron.oikoumene.io

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
    val bmls = for {
      y <- 0 to height - 1
    } yield bitmap.getSubimage(0, y, width, 1)
    val bmisiss = bmls.map { x => for { y <- 0 to width - 1 } yield x.getRGB(0, y) }
    val bmisas = bmisiss.map { _.toArray }
    bmisas.toArray
  }
  
  def readConfig(path: String): Reader = {
    new FileReader(path)
  }
}