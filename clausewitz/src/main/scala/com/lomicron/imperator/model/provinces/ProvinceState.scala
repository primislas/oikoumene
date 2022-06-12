package com.lomicron.imperator.model.provinces

import com.fasterxml.jackson.databind.node.ObjectNode

case class ProvinceState
(
  owner: Option[String] = None,
  controller: Option[String] = None,
  modifiers: Seq[ObjectNode] = Seq.empty,
)

object ProvinceState {
  val empty: ProvinceState = ProvinceState()
}
