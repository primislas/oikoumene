package com.lomicron.oikoumene.model.save.tag

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class TagAI
(
  // hits = 1179, isOptional = false, sample = false
  hreInterest: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = false
  initialized: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = false
  initializedAttitudes: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = "1444.11.11"
  lastRecalcDate: Date = Date.zero,
  // hits = 1179, isOptional = false, sample = true
  needsBuildings: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = false
  needsMoney: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = -1
  needsRegiments: BigDecimal = BigDecimal(0),
  // hits = 1179, isOptional = false, sample = true
  needsShips: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = false
  papacyInterest: Option[Boolean] = None,
  // hits = 1179, isOptional = false, sample = "nopersonality"
  personality: String = Entity.UNDEFINED,
  // hits = 1179, isOptional = false, sample = [0,0,0]
  powers: Color = Color.black,
  // hits = 1179, isOptional = false, sample = false
  static: Boolean = true,
  // hits = 591, isOptional = true, sample = 50.000
  treasury: Option[BigDecimal] = None,
  // hits = 585, isOptional = true, sample = [{"id":"MOS","value":400},{"id":"DAN","value":308},{"id":"BRA","value":105}]
  threat: Seq[JsonNode] = Seq.empty,
  // hits = 580, isOptional = true, sample = [{"id":"MOS","value":400},{"id":"TEU","value":118},{"id":"HAB","value":114},{"id":"HUN","value":106},{"id":"BOH","value":99},{"id":"KOL","value":74},{"id":"BRA","value":65},{"id":"SAX","value":65}]
  befriend: Seq[JsonNode] = Seq.empty,
  // hits = 528, isOptional = true, sample = {"id":"DAN","value":400}
  antagonize: Seq[JsonNode] = Seq.empty,
  // hits = 528, isOptional = true, sample = {"id":25,"value":240}
  conquerProv: Seq[JsonNode] = Seq.empty,
  // hits = 490, isOptional = true, sample = "SCA"
  powerBalanceThreat: Option[String] = None,
  // hits = 490, isOptional = true, sample = "SCA"
  powerBalanceThreatCache: Option[String] = None,
  // hits = 395, isOptional = true, sample = [{"id":"KLE","value":365},{"id":"BRA","value":280},{"id":"DNZ","value":148},{"id":"KOL","value":57},{"id":"MAG","value":54}]
  rival: Seq[JsonNode] = Seq.empty,
  // hits = 171, isOptional = true, sample = [{"id":"PRM","value":2800},{"id":"OIR","value":2800},{"id":"K02","value":2750},{"id":"BHA","value":2375},{"id":"BHT","value":750},{"id":"NOG","value":750},{"id":"TIM","value":750},{"id":"K01","value":375},{"id":"K00","value":375},{"id":"MNG","value":75},{"id":"MLC","value":25},{"id":"HAB","value":25}]
  militaryAccess: Seq[JsonNode] = Seq.empty,
  // hits = 47, isOptional = true, sample = 1928
  defendedHomeStrait: Option[Int] = None,
  // hits = 33, isOptional = true, sample = {"id":"DNZ","value":76}
  protect: Seq[JsonNode] = Seq.empty,
  // hits = 16, isOptional = true, sample = true
  defenderOfFaith: Boolean = false,
  // hits = 6, isOptional = true, sample = {"id":"HOL","value":400}
  vassal: Option[JsonNode] = None,
  // hits = 4, isOptional = true, sample = [{"id":4106,"value":1361},{"id":4108,"value":1318}]
  colonizeProv: Seq[JsonNode] = Seq.empty,
  // hits = 3, isOptional = true, sample = 1038
  migrate: Option[Int] = None,
  // hits = 1, isOptional = true, sample = [{"estate":"estate_rajput","target":50,"is_ceiling":false},{"estate":"estate_maratha","target":50,"is_ceiling":false},{"estate":"estate_nobles","target":50,"is_ceiling":false}]
  estateInfluence: Seq[JsonNode] = Seq.empty,
  // hits = 1, isOptional = true, sample = [{"id":"SUK","value":154},{"id":"MAM","value":128},{"id":"FRA","value":104},{"id":"SPA","value":94},{"id":"BRT","value":66},{"id":"BHA","value":61},{"id":"TID","value":32},{"id":"HAB","value":30},{"id":"SHY","value":27},{"id":"KZH","value":24},{"id":"TIM","value":22},{"id":"NOG","value":21},{"id":"PRM","value":19},{"id":"GBR","value":10}]
  tributaryState: Seq[JsonNode] = Seq.empty,
)

object TagAI extends FromJson[TagAI] {
  val empty: TagAI = new TagAI()
}
