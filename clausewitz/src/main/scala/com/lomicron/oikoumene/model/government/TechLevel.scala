package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.JsonProperty
import com.lomicron.oikoumene.model.modifiers.Modifier

import scala.collection.immutable.ListSet

case class TechLevel
(
  year: Int = 0,
  governments: Map[String, Boolean] = Map.empty,
  buildings: Map[String, Boolean] = Map.empty,
  @JsonProperty("enable")
  units: ListSet[String] = ListSet.empty,
  modifier: Option[Modifier] = None,
)
