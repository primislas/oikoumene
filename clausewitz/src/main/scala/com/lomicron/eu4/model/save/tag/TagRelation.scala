package com.lomicron.eu4.model.save.tag

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.JsonNode
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class TagRelation
(
  // hits = 129153, isOptional = true, sample = true
  hasChanged: Boolean = false,
  // hits = 17817, isOptional = true, sample = "attitude_neutral"
  attitude: Option[String] = None,
  // hits = 4780, isOptional = true, sample = true
  hasCultureGroupClaim: Boolean = false,
  // hits = 2294, isOptional = true, sample = "1741.5.16"
  lastSendDiplomat: Option[Date] = None,
  // hits = 2113, isOptional = true, sample = 49
  trustValue: Option[Int] = None,
  // hits = 1639, isOptional = true, sample = {"modifier":"improved_relation","date":"1741.4.16","current_opinion":26.000,"expiry_date":true}
  opinion: Seq[JsonNode] = Seq.empty,
  // hits = 1638, isOptional = true, sample = 26
  cachedSum: Option[BigDecimal] = None,
  // hits = 1552, isOptional = true, sample = "1736.7.24"
  lastWar: Option[Date] = None,
  // hits = 1260, isOptional = true, sample = 68
  lastWarscore: Option[Int] = None,
  // hits = 681, isOptional = true, sample = true
  recalcAttitude: Boolean = false,
  // hits = 468, isOptional = true, sample = true
  hasCoreClaim: Boolean = false,
  // hits = 331, isOptional = true, sample = "1746.8.4"
  lastSpyDiscovery: Option[Date] = None,
  // hits = 223, isOptional = true, sample = 41
  favors: Option[Int] = None,
  // hits = 145, isOptional = true, sample = "1722.9.3"
  lastOffensiveCall: Option[Date] = None,
  // hits = 144, isOptional = true, sample = 4438
  spyNetwork: Option[Int] = None,
  // hits = 133, isOptional = true, sample = true
  hasClaim: Boolean = false,
  // hits = 131, isOptional = true, sample = true
  truce: Boolean = false,
  // hits = 129, isOptional = true, sample = true
  isBuildingSpyNetwork: Boolean = false,
  // hits = 111, isOptional = true, sample = 4
  isFightingWarTogether: Option[Int] = None,
  // hits = 107, isOptional = true, sample = true
  hasColonyClaim: Boolean = false,
  // hits = 89, isOptional = true, sample = true
  isEmbargoing: Boolean = false,
  // hits = 79, isOptional = true, sample = 0
  lastWarStatus: Option[Int] = None,
  // hits = 4, isOptional = true, sample = 2
  playerAttitude: Option[Int] = None,
)

object TagRelation extends FromJson[TagRelation]
