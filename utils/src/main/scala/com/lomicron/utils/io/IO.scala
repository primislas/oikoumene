package com.lomicron.utils.io

import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Path, Paths}

import scala.io.{Codec, Source}
import scala.language.reflectiveCalls

object IO {

  /**
    *
    * @param resource resource to be accessed
    * @param cleanup  resource cleanup function
    * @param doWork   resource processing function
    * @return
    */
  def cleanly[A, B](resource: A)(cleanup: A => Unit)(doWork: A => B): B = {
    try {
      doWork(resource)
    } finally {
      cleanup(resource)
    }
  }

  def using[A <: {def close() : Unit}, B](resource: A)(f: A => B): B =
    try {
      f(resource)
    } finally {
      resource.close()
    }

  def readTextFile(path: String): String =
    readTextFile(path, StandardCharsets.ISO_8859_1)

  def readTextFile(path: String, charset: Charset): String =
    cleanly(Source.fromFile(path)(Codec.apply(charset)))(_.close())(_.mkString)

  //using(Source.fromFile(path))(_.mkString)

  def readTextResource(path: String): String =
    cleanly(Source.fromURL(getClass.getClassLoader.getResource(path))(Codec.apply(StandardCharsets.ISO_8859_1)))(_.close)(_.mkString)

  def listFiles(path: String): Seq[String] = {
    Option(path)
      .map(Paths.get(_))
      .map(listFiles)
      .getOrElse(Seq.empty)
  }

  def listFiles(path: Path): Seq[String] = {
    Option(path)
      .map(_.toFile)
      .filter(_.isDirectory)
      .map(_.listFiles)
      .map(_.toSeq)
      .map(_.map(_.getAbsolutePath))
      .getOrElse(Seq.empty)
  }

}
