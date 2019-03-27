package com.lomicron.oikoumene.model.localisation

case class Localisation(name: Option[String] = None, adjective: Option[String] = None) {
  def this() = this(None, None)
}

object Localisation {
  val empty = Localisation()
}

trait WithLocalisation {
  val localisation: Localisation
}