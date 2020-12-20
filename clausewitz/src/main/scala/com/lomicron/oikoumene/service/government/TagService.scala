package com.lomicron.oikoumene.service.government

import com.lomicron.oikoumene.model.government.TechLevel
import com.lomicron.oikoumene.model.politics.{Tag, TagState}
import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils.toOption

case class TagService(repos: RepositoryFactory) {

  type ToggleMap = Map[String, Boolean]
  val toggleMap: ToggleMap = Map.empty

  /** Need at least this much development to upgrade to government rank 2 */
  val MIN_DEVELOPMENT_FOR_GOV_RANK_2 = 300
  /** Need at least this much development to upgrade to government rank 3 */
  val MIN_DEVELOPMENT_FOR_GOV_RANK_3 = 1000

  def init(t: Tag): Tag = {
    val tag = t.id
    val provs = repos.provinces.findAll.filter(t => t.state.owner.contains(tag) || t.state.controller.contains(tag))
    val ownedProvs = provs.filter(_.state.owner.contains(tag))

    var its = t.state
    its = initTechnology(its)
    val statics = repos.modifiers.static

    // TODO: primitive_nation
    val allNations = statics.allNations
    val base = statics.baseValues
    its = initGovRank(its, ownedProvs)
    val rank = its.governmentRank.flatMap(statics.governmentRank)
    val gov = its.government
      .flatMap(repos.governments.find(_).toOption)
      .map(_.basicReform)
      .flatMap(repos.governmentReforms.find(_).toOption)
      .flatMap(_.modifierWithId)
    val reforms = its
      .governmentReforms
      .flatMap(repos.governmentReforms.find(_).toOption)
      .flatMap(_.modifierWithId)
    val traditions = repos.ideas.ofTag(tag).flatMap(_.startWithId)
    // TODO production efficiency
    val legitimacy = statics.legitimacy.map(_ * ((its.legitimacy - 50).doubleValue() / 100))
    val prestige = statics.prestige.map(_ * its.prestige)
    val stabLvl = its.stability
    val stab = statics.stability.map(_ * stabLvl)
    val positiveStab = statics.positiveStability.map(_ * (if (stabLvl > 0) stabLvl else BigDecimal(0)))
    val negativeStab = statics.negativeStability.map(_ * (if (stabLvl < 0) -stabLvl else BigDecimal(0)))
    val mercantilism = its.mercantilism.flatMap(v => statics.mercantilism.map(m => m * v))
    val cots = repos.centersOfTrade.globalModifiers(ownedProvs)
    val armyTrad = statics.armyTradition.map(_ * its.armyTradition)
    val navyTrad = statics.navyTradition.map(_ * its.navyTradition)
    // TODO additional tag modifiers
    //  active modifiers
    //  ruler modifiers
    //  val monarch = ts.monarch.flatMap(_.personalities)
    val modifiers = Seq(
      allNations, base, rank, gov, traditions,
      legitimacy, prestige, stab, positiveStab, negativeStab,
      mercantilism, armyTrad, navyTrad
    ).flatten ++ reforms ++ cots
    its = modifiers.foldLeft(its)(_ addModifier _)

    // TODO further tag initialization
    // val totalProduction = provs.map(_.state.production).foldLeft(ProvinceProduction.empty)(_ + _)
    // get religious modifiers ?
    // init states
    // init estates
    // institutions
    // diplo relations
    //   num_of_marriages
    //   war, peace
    //   subject_nation, vassal_nation

    t.withState(its)
  }

  def initGovRank(state: TagState, ownedProvinces: Seq[Province]): TagState = {
    if (state.governmentRank.isDefined) state
    else {
      val dev = ownedProvinces.map(_.state.development).sum
      val rank =
        if (dev > MIN_DEVELOPMENT_FOR_GOV_RANK_3) 3
        else if (dev > MIN_DEVELOPMENT_FOR_GOV_RANK_2) 2
        else 1
      state.copy(governmentRank = rank)
    }
  }

  def initTechnology(ts: TagState): TagState = {
    val techLevels = repos.technology.groups
      .find(ts.technologyGroup).toOption
      .map(tg => tg.startLevel)
      .flatMap(allTechnologiesAtLevel)
      .getOrElse(Seq.empty)
    val buildings = techLevels.map(_.buildings).foldLeft(toggleMap)(_ ++ _)
    val govs = techLevels.map(_.governments).foldLeft(toggleMap)(_ ++ _)
    val modifiers = techLevels.flatMap(_.modifier)

    val withTech = modifiers.foldLeft(ts)(_ addModifier _)
    val withBuildings = withTech.toggleBuildings(buildings)
    val withGovs = withBuildings.toggleGovernments(govs)

    withGovs
  }

  def allTechnologiesAtLevel(level: Int): Seq[TechLevel] =
    repos.technology.findAll.flatMap(_.levels.take(level))

}
