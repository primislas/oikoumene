package com.lomicron.oikoumene.model.localisation

case class Localisation(name: String)

trait WithLocalisation {
  val localisation: Localisation
}