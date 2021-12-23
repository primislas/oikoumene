package com.lomicron.eu4.model

import com.lomicron.eu4.model.modifiers.{ActiveModifier, Modifier}

trait WithCumulativeModifier[T <: WithCumulativeModifier[T]] {

  val activeModifiers: Map[String, ActiveModifier] = Map.empty
  val modifier: Modifier = Modifier()

  def self: T
  def withModifiers(activeModifiers: Map[String, ActiveModifier], cumulative: Modifier): T

  def addModifier(am: ActiveModifier): T = {
    val id = am.name
    val actives = activeModifiers + (id -> am)
    val cumulative = am.effect.map(modifier.add).getOrElse(modifier)
    withModifiers(actives, cumulative)
  }

  def addModifier(m: Modifier): T =
    m.id
      .map(id => ActiveModifier(name = id, effect = Some(m)))
      .map(addModifier)
      .getOrElse(self)

  def removeModifier(m: Modifier): T =
    m.id
      .map(id => {
        val actives = activeModifiers - id
        val cumulative = modifier.remove(m)
        withModifiers(actives, cumulative)
      })
      .getOrElse(self)

}
