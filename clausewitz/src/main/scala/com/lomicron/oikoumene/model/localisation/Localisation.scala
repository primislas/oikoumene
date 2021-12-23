package com.lomicron.oikoumene.model.localisation

import com.lomicron.eu4.service.NamingService
import com.lomicron.utils.json.{FromJson, ToJson}

case class Localisation(name: Option[String] = None, adjective: Option[String] = None) extends ToJson {

  private var locAliases: Set[String] = Set.empty

  def this() = this(None, None)

  def matches(s: String): Boolean = {
    aliases.exists(_.contains(s))
  }

  def aliases: Set[String] = {
    if (locAliases.isEmpty) name.map(NamingService.makeAliases).foreach(as => locAliases = as)
    locAliases
  }

}

object Localisation extends FromJson[Localisation] {
  val empty: Localisation = Localisation()
}

trait WithLocalisation {
  val localisation: Localisation
  def name: Option[String] = localisation.name
  def isNamed(name: String): Boolean = localisation.name.contains(name)
}
