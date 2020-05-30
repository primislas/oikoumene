package com.lomicron.oikoumene.mods

import com.lomicron.oikoumene.model.politics.{Monarch, Tag, TagHistory, TagUpdate}
import com.lomicron.oikoumene.model.provinces.{Province, ProvinceState, ProvinceUpdate}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.{ProvinceRepository, ProvinceSearchConf}
import com.lomicron.oikoumene.repository.api.politics.TagSearchConf
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.parsing.tokenizer.Date
import com.typesafe.scalalogging.LazyLogging

object ModUtils extends LazyLogging {

  def updateReligionsOfSuperregion
  (superRegion: String, religions: Map[String, String], repos: RepositoryFactory)
  : (Seq[Province], Seq[Tag]) = {
    val ps = findProvincesBySuperRegion(superRegion, repos)
    updateProvinceAndTagReligions(repos, ps, religions)
  }

  def updateReligionsOfRegions
  (regions: Seq[String], religions: Map[String, String], repos: RepositoryFactory)
  : (Seq[Province], Seq[Tag]) = {
    val ps = regions.flatMap(findProvincesByRegion(_, repos))
    updateProvinceAndTagReligions(repos, ps, religions)
  }

  def updateReligionsOfCultures
  (cultures: Seq[String], religions: Map[String, String], repos: RepositoryFactory)
  : (Seq[Province], Seq[Tag]) =
    cultures
      .map(updateReligionsOfCulture(_, religions, repos))
      .reduce(mergeUpdates)

  def mergeUpdates(t1: (Seq[Province], Seq[Tag]), t2: (Seq[Province], Seq[Tag])): (Seq[Province], Seq[Tag]) =
    (t1._1 ++ t2._1, t1._2 ++ t2._2)

  def updateReligionsOfCulture
  (culture: String, religions: Map[String, String], repos: RepositoryFactory)
  : (Seq[Province], Seq[Tag]) = {
    val ps = findProvincesByCulture(culture, repos.provinces)
    updateProvinceAndTagReligions(repos, ps, religions)
  }

  def updateProvinceAndTagReligions(repos: RepositoryFactory, ps: Seq[Province], religions: Map[String, String]): (Seq[Province], Seq[Tag]) = {
    def moddedTagReligion(t: Tag): Option[String] = t.state.religion.flatMap(religions.get)

    def modTagReligion(t: Tag): Option[Tag] = moddedTagReligion(t).map(r => tagWithReligion(t, r))

    def moddedProvReligion(p: Province): Option[String] = p.state.religion.flatMap(religions.get)

    def modProvReligion(p: Province): Option[Province] = moddedProvReligion(p).map(r => provinceWithReligion(p, r))

    val provinceIds = ps.map(_.id).toSet
    val tagIds = ps.flatMap(_.state.owner).distinct
    val modifiedTags = repos.tags.find(tagIds)
      .filter(t => t.state.capital.exists(provinceIds.contains))
      .flatMap(modTagReligion)
    val modifiedProvinces = ps.flatMap(modProvReligion).map(dropHistory)

    modifiedTags.foreach(t => {
      val (id, name, religion) = (t.id, t.localisation.name.getOrElse("UNKNOWN"), t.state.religion)
      logger.info(s"Modded $id ($name) to $religion")
    })

    val psByRel = modifiedProvinces.groupBy(_.history.init.religion.get)
    psByRel.foreach(rps => logger.info(s"Modded ${rps._2.size} provinces to ${rps._1}"))

    repos.provinces.update(modifiedProvinces)
    repos.tags.update(modifiedTags)

    (modifiedProvinces, modifiedTags)
  }

  def removeCore(p: Province, core: String): Province = {
    val cores = p.history.init.addCore.map(cs => cs.filterNot(_ == core)).getOrElse(Seq.empty)

    def coresToSet(cs: Option[Seq[String]]) = cs.map(_.toSet).getOrElse(Set.empty)

    updateProvHistAndRemoveFromEvents[Seq[String]](p, cores, (u, v) => u.copy(addCore = v), (u, v) => u.copy(cores = coresToSet(v)))
  }

  def writeProvinces(repos: RepositoryFactory, mod: String, provinces: Seq[Province]): Seq[Province] = {
    repos
      .modWriters(mod)
      .provinceHistoryWriter
      .clear
      .storeEntities(provinces)

    provinces
  }

  def writeCountries(repos: RepositoryFactory, mod: String, tags: Seq[Tag]): Seq[Tag] = {
    repos
      .modWriters(mod)
      .tagHistoryWriter
      .clear
      .storeEntities(tags)

    tags
  }

  def findOwnerTag(p: Province, repos: RepositoryFactory): Option[Tag] =
    p.state.owner.map(repos.tags.find).flatMap(_.toOption)

  def findProvPrimaryTag(p: Province, repos: RepositoryFactory): Option[Tag] = {
    p.state.cores
      .flatMap(repos.tags.find(_).toOption)
      .find(t => t.state.primaryCulture.exists(p.state.culture.contains))
  }

  def findAreaProvinces(areaName: String, repos: RepositoryFactory): Seq[Province] =
    Option(areaName)
      .map(n => if (!n.endsWith("_area")) s"${n}_area" else n)
      .map(repos.geography.areas.find)
      .flatMap(_.toOption)
      .map(_.provinceIds)
      .map(repos.provinces.find)
      .getOrElse(Seq.empty)

  def findTagByName(name: String, repos: RepositoryFactory): Option[Tag] =
    repos.tags.search(TagSearchConf.ofName(name)).entities.headOption

  def findProvinceByName(name: String, repos: RepositoryFactory): Option[Province] =
    repos.provinces.search(ProvinceSearchConf.ofName(name)).entities.headOption

  def findProvincesByCulture(culture: String, provinces: ProvinceRepository): Seq[Province] =
    provinces.search(ProvinceSearchConf.ofCulture(culture).all).entities

  def findProvincesByCore(core: String, provinces: ProvinceRepository): Seq[Province] =
    provinces.search(ProvinceSearchConf.empty.copy(core = Some(core)).all).entities

  def findProvincesBySuperRegion(superRegion: String, repos: RepositoryFactory): Seq[Province] =
    repos.geography.superregions.find(superRegion).toOption.toSeq
      .flatMap(_.regionIds)
      .flatMap(findProvincesByRegion(_, repos))

  def findProvincesByRegion(region: String, repos: RepositoryFactory): Seq[Province] =
    repos.geography.regions.find(region)
      .toOption.toSeq
      .flatMap(_.areas)
      .flatMap(repos.geography.areas.find(_).toOption)
      .flatMap(_.provinceIds)
      .flatMap(repos.provinces.find(_).toOption)

  def provinceWith(p: Province, owner: String, religion: String): Province = {
    val withOwner = provinceWithOwner(p, owner)
    provinceWithReligion(withOwner, religion)
  }

  def provinceWithOwner(p: Province, owner: String): Province = {
    val origOwner = p.state.owner

    val withOwner = updateProvHistAndRemoveFromEvents[String](p, owner, (u, v) => u.copy(owner = v), (u, v) => u.copy(owner = v))
    val withController = updateProvHistAndRemoveFromEvents[String](withOwner, owner, (u, v) => u.copy(controller = v), (u, v) => u.copy(controller = v))
    val withCores = updateProvHistAndRemoveFromEvents[String](
      withController,
      owner,
      (u, v) => v.map(u.addCoreCp).getOrElse(u),
      (u, v) => v.map(u.addCore).getOrElse(u))
    if (!origOwner.contains(owner) && origOwner.isDefined) {
      val origCores = withCores.state.cores - origOwner.get
      updateProvHistAndRemoveFromEvents[Seq[String]](withCores, origCores.toSeq, (u, v) => u.copy(addCore = v), (u, _) => u.copy(cores = origCores))
    } else withCores
  }

  def provinceWithReligion(p: Province, religion: String): Province =
    updateProvHistAndRemoveFromEvents[String](p, religion, (u, v) => u.copy(religion = v), (u, v) => u.copy(religion = v))

  def provinceWithCulture(p: Province, culture: String): Province =
    updateProvHistAndRemoveFromEvents[String](p, culture, (u, v) => u.copy(culture = v), (u, v) => u.copy(culture = v))

  def updateProvHistAndRemoveFromEvents[T]
  (
    p: Province,
    v: T,
    setUpdate: (ProvinceUpdate, Option[T]) => ProvinceUpdate,
    updateState: (ProvinceState, Option[T]) => ProvinceState
  ): Province = {
    val hist = p.history
    val init = setUpdate(hist.init, Option(v))
    val events = hist.events.map(setUpdate(_, None)).filter(_.isNotEmpty)
    val state = updateState(hist.state, Option(v))
    val updatedHist = hist.copy(init = init, events = events, state = state)
    p.copy(history = updatedHist)
  }

  def dropHistory(p: Province): Province = {
    val currOwner = p.state.owner
    val currController = p.state.controller
    val initOwner = p.history.init.copy(owner = currOwner, controller = currController)
    p.copy(history = p.history.copy(init = initOwner, events = Seq.empty))
  }

  def dropHistory(t: Tag): Tag = t.copy(history = t.history.copy(events = Seq.empty))

  def tagWithReligion(tag: Tag, religion: String): Tag =
    Option(tag)
      .map(t => t.copy(history = t.history.updateState(_.setReligion(religion))))
      .map(t => t.copy(history = t.history.copy(init = t.history.init.copy(religion = Option(religion)))))
      .map(t => t.copy(history = dropTagHistoryReligion(t.history)))
      .get

  def dropTagHistoryReligion(h: TagHistory): TagHistory = {
    val withoutReligionUpdates = h.events.map(dropTagEventReligion)
    h.copy(events = withoutReligionUpdates)
  }

  def dropTagEventReligion(e: TagUpdate): TagUpdate = {
    val monarch = e.monarch.map(_.copy(religion = None))
    val queen = e.queen.map(_.copy(religion = None))
    val heir = e.heir.map(_.copy(religion = None))
    e.copy(religion = None, monarch = monarch, queen = queen, heir = heir)
  }

  def tagWithCulture(tag: Tag, culture: String): Tag =
    Option(tag)
      .map(t => t.copy(history = t.history.updateState(_.setPrimaryCulture(culture))))
      .map(t => t.copy(history = t.history.copy(init = t.history.init.copy(primaryCulture = Option(culture)))))
      .get

  def addMonarch(t: Tag, m: Monarch, date: Date, personalities: Seq[String]): Tag = {
    val coronation = TagUpdate(date = date, monarch = m)
    val oPersonalities = Option(personalities).filter(_.nonEmpty)
    val clrPersonalities = oPersonalities.map(_ => TagUpdate(date = date, clearScriptedPersonalities = true))
    val addPersonalities = oPersonalities.map(ps => TagUpdate(date = date, addRulerPersonality = ps))
    val events = Seq(coronation) ++ Seq(clrPersonalities, addPersonalities).flatten

    t.copy(history = t.history.addEvents(events))
  }

  def removeDhimmiEstate(p: Province): Province = {
    if (p.state.estate.contains("estate_dhimmi"))
      p.updateInitState(_.copy(estate = None))
    else p
  }
}
