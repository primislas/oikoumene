package com.lomicron.oikoumene.model.localisation

import scala.util.matching.Regex

case class LocalisationEntry
(key: String, version: Int, text: String)

object LocalisationEntry {
  val localisationPattern: Regex =
    "^\\s*(?<key>\\w+):(?<version>\\d+)\\s*\"(?<text>.*)\"".r

  def fromString(str: String): Option[LocalisationEntry] =
    str match {
      case localisationPattern(key, version, text) => Some(LocalisationEntry(key, version.toInt, text))
      case _ => None
    }

  def fromStrings(strs: Seq[String]): Seq[LocalisationEntry] =
    strs.flatMap(fromString)

}
