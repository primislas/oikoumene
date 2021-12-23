package com.lomicron.eu4.service

object NamingService {

  val commonCharSwaps = Seq("ä" -> "a", "ö" -> "o", "ü" -> "u", "ß" -> "s",
    "á" -> "a", "é" -> "e", "í" -> "i", "ó" -> "o", "ú" -> "u", "ñ" -> "n")


  def makeAliases(name: String): Set[String] = {
    val lc = name.toLowerCase
    val common = commonCharSwaps.foldLeft(lc)((s, t) => s.replace(t._1, t._2))
    Set(common)
  }

}
