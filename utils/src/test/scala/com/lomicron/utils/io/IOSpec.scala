package com.lomicron.utils.io

import java.io.FileNotFoundException

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification

class IOSpec extends Specification with Mockito {
  //val file = mock[File]
  val validPath = "151 - Constantinople.txt"
  val invalidPath = "NoSuchFileExists.file"
  
  "IO#readTextResource" should {
    "read resource if available" >> {
      val content = IO.readTextResource(validPath)
      content must have size 1719
    }
  }

  "IO#readTextFile" should {
    "throw an exception if no file is found" >> {
      IO.readTextFile(invalidPath) must throwA[FileNotFoundException]
    }
  }
  
  
}