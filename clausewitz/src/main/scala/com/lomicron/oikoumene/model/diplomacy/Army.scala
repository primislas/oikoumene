package com.lomicron.oikoumene.model.diplomacy

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.utils.json.FromJson

case class Army
(
  id: Option[Int] = None,
  // hits = 694, isOptional = false, sample = "Francois de Vendome"
  commander: String = Entity.UNDEFINED,
  // hits = 694, isOptional = false, sample = "FRA"
  country: String = Entity.UNDEFINED,
  // hits = 694, isOptional = false, sample = 12
  losses: Int = 0,
  // hits = 615, isOptional = true, sample = 15000
  infantry: Option[Int] = None,
  // hits = 263, isOptional = true, sample = 2000
  cavalry: Option[Int] = None,
  // hits = 145, isOptional = true, sample = 20
  artillery: Option[Int] = None,
  // hits = 59, isOptional = true, sample = 80
  heavyShip: Option[Int] = None,
  // hits = 42, isOptional = true, sample = 60
  lightShip: Option[Int] = None,
  // hits = 12, isOptional = true, sample = 80
  galley: Option[Int] = None,
) {
  @JsonCreator def this() = this(None)
}

object Army extends FromJson[Army] {
  def empty: Army = new Army()
}