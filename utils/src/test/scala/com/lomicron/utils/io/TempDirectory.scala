package com.lomicron.utils.io

import java.io.{ File, FileOutputStream }
import java.util.UUID

import org.specs2.execute.AsResult

object TempDirectory {
  def apply[R: AsResult](a: File ⇒ R) = {
    val temp = createTemporaryDirectory("")
    try {
      AsResult.effectively(a(temp))
    } finally {
      removeTemporaryDirectory(temp)
    }
  }

  /** Creates a new temporary directory and returns it's location. */
  def createTemporaryDirectory(suffix: String): File = {
    val base = new File(new File(System.getProperty("java.io.tmpdir")), "test-tmp-dir")
    val dir = new File(base, UUID.randomUUID().toString + suffix)
    dir.mkdirs()
    dir
  }

  /** Removes a directory (recursively). */
  def removeTemporaryDirectory(dir: File): Unit = {
    def rec(f: File): Unit = {
      if (f.isDirectory) {
        f.listFiles().foreach(rec(_))
      }
      f.delete()
    }
    rec(dir)
  }
}