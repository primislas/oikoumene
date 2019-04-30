package com.lomicron.oikoumene.model.events

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson

case class TagCondition
(
  // hits = 20, isOptional = true, sample = {"truce_with":"FROM","num_of_cities":2,"FROM":{"num_of_cities":2}}
  not: Option[TagCondition] = None,
  // hits = 17, isOptional = true, sample = {"government":"monarchy","has_reform":"dutch_republic"}
  or: Option[TagCondition] = None,
  // hits = 4, isOptional = true, sample = [{"government":"monarchy","FROM":{"government":"republic"}},{"government":"republic","NOT":{"FROM":{"government":"republic"}}}]
  and: Seq[TagCondition] = Seq.empty,
  // hits = 27, isOptional = true, sample = {"government":"monarchy","is_subject":false}
  from: Option[TagCondition] = None,

  // triggers also have factor and modifier fields,
  // but other than that they appear to be ordinary tag conditions
  // hits = 72, isOptional = true, sample = 0.45
  factor: Option[BigDecimal] = None,
  // hits = 13, isOptional = true, sample = {"factor":0.5,"is_subject":true}
  modifier: Seq[TagCondition] = Seq.empty,

  // hits = 35, isOptional = true, sample = false
  isRevolutionTarget: Option[Boolean] = None,

  // hits = 15, isOptional = true, sample = "FROM"
  isNeighborOf: Option[String] = None,
  // hits = 10, isOptional = true, sample = "Conquest of Paradise"
  hasDlc: Option[String] = None,
  // hits = 9, isOptional = true, sample = {"is_part_of_hre":false}
  capitalScope: Option[ProvinceCondition] = None,
  // hits = 9, isOptional = true, sample = "war_against_the_world_doctrine_reform"
  hasReform: Seq[String] = Seq.empty,
  // hits = 9, isOptional = true, sample = true
  isFreeOrTributaryTrigger: Option[Boolean] = None,
  // hits = 7, isOptional = true, sample = true
  isSubject: Option[Boolean] = None,
  // hits = 6, isOptional = true, sample = "native"
  government: Option[String] = None,
  // hits = 4, isOptional = true, sample = true
  isEmperor: Option[Boolean] = None,
  // hits = 4, isOptional = true, sample = true
  isNomad: Option[Boolean] = None,
  // hits = 4, isOptional = true, sample = "catholic"
  religion: Option[String] = None,
  // hits = 3, isOptional = true, sample = true
  cbOnReligiousEnemies: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = 23
  dipTech: Option[Int] = None,
  // hits = 3, isOptional = true, sample = false
  hreReligionTreaty: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = "FROM"
  isSubjectOf: Option[String] = None,
  // hits = 3, isOptional = true, sample = "from"
  religionGroup: Seq[String] = Seq.empty,
  // hits = 2, isOptional = true, sample = false
  ai: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = {"is_part_of_hre":true}
  anyOwnedProvince: Option[ProvinceCondition] = None,
  // hits = 2, isOptional = true, sample = "FROM"
  hasMatchingReligion: Option[String] = None,
  // hits = 2, isOptional = true, sample = "FROM"
  isColonialNationOf: Option[String] = None,
  // hits = 2, isOptional = true, sample = true
  isEmperorOfChina: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = false
  isInLeagueWar: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = false
  isReligionReformed: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = true
  isTradeLeagueLeader: Option[Boolean] = None,
  // hits = 2, isOptional = true, sample = "FROM"
  juniorUnionWith: Option[String] = None,
  // hits = 9, isOptional = true, sample = 2
  numOfCities: Option[Int] = None,
  // hits = 2, isOptional = true, sample = {"trade_league_embargoed_by":"FROM"}
  root: Option[Object] = None,
  // hits = 2, isOptional = true, sample = "FROM"
  truceWith: Option[String] = None,
  // hits = 2, isOptional = true, sample = "FROM"
  vassalOf: Option[String] = None,
  // hits = 1, isOptional = true, sample = false
  always: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = {"is_elector":true}
  anySubjectCountry: Option[Object] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  canJustifyTradeConflict: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  canMigrate: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  cbOnGovernmentEnemies: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  claim: Option[String] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  coalitionTarget: Option[String] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  colonyClaim: Option[String] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  coreClaim: Option[String] = None,
  // hits = 1, isOptional = true, sample = {"tag":"FROM"}
  crusadeTarget: Option[Object] = None,
  // hits = 1, isOptional = true, sample = "east_asian"
  cultureGroup: Option[String] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  cultureGroupClaim: Option[String] = None,
  // hits = 1, isOptional = true, sample = {"modifier":"aggressive_expansion","who":"FROM"}
  hasOpinionModifier: Option[Object] = None,
  // hits = 1, isOptional = true, sample = true
  hasPrivateers: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  hasSpawnedSupportedRebels: Option[String] = None,
  // hits = 1, isOptional = true, sample = "ROOT"
  hreHereticReligion: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  invasionNation: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  isDefenderOfFaith: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  isElector: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = false
  isExcommunicated: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  isImperialBanAllowed: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = true
  isInCoalitionWar: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  isLeagueEnemy: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  isLeagueLeader: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  isRival: Option[String] = None,
  // hits = 1, isOptional = true, sample = true
  isSubjectOtherThanTributaryTrigger: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = 3
  numOfCoalitionMembers: Option[Int] = None,
  // hits = 1, isOptional = true, sample = [{"war_with":"ROOT"},{"truce_with":"ROOT"},{"alliance_with":"ROOT"}]
  revolutionTarget: Seq[TagCondition] = Seq.empty,
  // hits = 1, isOptional = true, sample = true
  revolutionTargetExists: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "FROM"
  successionClaim: Option[String] = None,
  // hits = 1, isOptional = true, sample = ["ENG","GBR"]
  tag: Seq[String] = Seq.empty,


  // hits = 6, isOptional = true, sample = 5
  numOfPorts: Option[Int] = None,
  // hits = 3, isOptional = true, sample = "humanist_ideas"
  hasIdeaGroup: Option[String] = None,
  // hits = 3, isOptional = true, sample = true
  isTribal: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = true
  primitives: Option[Boolean] = None,
  // hits = 3, isOptional = true, sample = "high_american"
  technologyGroup: Seq[String] = Seq.empty,
  // hits = 3, isOptional = true, sample = 2
  vassal: Option[Int] = None,
  // hits = 2, isOptional = true, sample = 5
  inflation: Option[Int] = None,
  // hits = 2, isOptional = true, sample = true
  isColonialNation: Option[Boolean] = None,
  // hits = 1, isOptional = true, sample = "is_merchant_republic"
  hasGovernmentAttribute: Option[String] = None,
  // hits = 1, isOptional = true, sample = 1490
  isYear: Option[Int] = None,

) {
  @JsonCreator def this() = this(None)
}

object TagCondition extends FromJson[TagCondition]