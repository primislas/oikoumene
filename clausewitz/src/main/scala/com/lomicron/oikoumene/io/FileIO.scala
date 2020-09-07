package com.lomicron.oikoumene.io

import java.awt.image.BufferedImage
import java.io._
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Path, Paths}

import com.lomicron.utils.collection.CollectionUtils.OptionEx
import javax.imageio.ImageIO

import scala.util.{Failure, Success, Try}

object FileIO {
  self =>

  val defaultCharset: Charset = StandardCharsets.ISO_8859_1

  def cleanly[A, B](resource: A)(cleanup: A => Unit)(doWork: A => B): Try[B] = {
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
    for {y <- 0 until bitmap.getWidth} yield bitmap.getRGB(0, y)

  def readConfig(path: String): Reader = {
    new FileReader(path)
  }

  def ensureDirsExist(p: Path): Option[Path] = {
    Option(p)
      .map(_.toFile)
      .filterNot(_.exists())
      .foreach(_.mkdirs())
    Option(p)
  }

  def ensureDirsExist(f: File): Option[File] = {
    Option(f)
      .filterNot(_.exists())
      .peek(_.mkdirs())
      .orElse(Option(f))
  }

  def writeUTF(p: Path, f: FileNameAndContent): Try[Unit] = write(p, f, StandardCharsets.UTF_8)

  def writeLatin(p: Path, f: FileNameAndContent): Try[Unit] = write(p, f)

  def write(p: Path, f: FileNameAndContent, charset: Charset = defaultCharset): Try[Unit] =
    Option(p)
      .map(p => Paths.get(p.toString, f.name))
      .map(_.toFile)
      .map(write(_, f.content, charset))
      .getOrElse(Try())

  def write(f: File, content: String, charset: Charset): Try[Unit] = {
    val stream = new FileOutputStream(f)
    val streamWriter = new OutputStreamWriter(stream, charset)
    val bufferedWriter = new BufferedWriter(streamWriter)
    cleanly(bufferedWriter)(_.close())(_.write(content))
  }

  def clearDir(dir: String): Option[File] =
    Option(dir)
      .map(Paths.get(_))
      .flatMap(clearDir)

  def clearDir(p: Path): Option[File] =
    Option(p).map(_.toFile).flatMap(clearDir)

  def clearDir(f: File): Option[File] =
    Option(f)
      .map(of => if (of.isDirectory) of else of.getParentFile)
      .flatMap(ensureDirsExist)
      .peek(_.listFiles().foreach(_.delete()))


}
