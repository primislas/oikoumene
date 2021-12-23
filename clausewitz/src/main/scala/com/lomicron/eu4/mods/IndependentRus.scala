package com.lomicron.eu4.mods

import com.lomicron.eu4.model.politics.{Monarch, Tag, TagHistory, TagUpdate}
import com.lomicron.eu4.model.provinces.Province
import com.lomicron.eu4.mods.ModUtils._
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.map.ProvinceSearchConf
import com.lomicron.eu4.repository.api.politics.TagRepository
import com.lomicron.oikoumene.model.localisation.Localisation
import com.typesafe.scalalogging.LazyLogging
import com.lomicron.utils.collection.CollectionUtils.toOption
import com.lomicron.utils.parsing.tokenizer.Date

object IndependentRus extends LazyLogging {
  val mod = "rus"
  val eastSlavic = "east_slavic"
  val ruthenian = "ruthenian"
  val lytvyn = "byelorussian"
  val muscovite = "russian"
  val orthodox = "orthodox"
  val zaporizhiaArea = "zaporizhia_area"
  val minskArea = "minsk_area"
  val turovArea = "pripyat_area"
  val chernigovArea = "chernigov_area"
  val slobodaArea = "sloboda_ukraine_area"
  val rusSubjugators = Set("HUN", "MAZ", "MOS", "LIT", "POL", "CRI")
  val coresByProvName = Map(
    "Yuriev" -> "RSO",
    "Vladimir" -> "RSO",
    "Suzdal" -> "RSO",
    "Murom" -> "NZH",
    "Bezhetsk" -> "BLO",
    "Vologda" -> "BLO",
    "Velsk" -> "BLO",
    "Totma" -> "BLO",
    "Ustyug" -> "BLO",
    "Sol Galitskaya" -> "BLO",
    "Kostroma" -> "YAR",
    "Galich" -> "YAR",
    "Vetluga" -> "YAR",
    "Viatka" -> "PRM",
    "Luki" -> "PSK",
    "Ingermanland" -> "PSK",
    "Toropets" -> "TVE",
    "Kasimov" -> "QAS",
    "Penza" -> "QAS",
    "Alatyr" -> "QAS",
    "Chelm" -> "VOL",
    "Kharkov" -> "CHR",
  )
  val muscovitePs = Set("Ryazan", "Pronsk", "Tula", "Oka")

  def apply(repos: RepositoryFactory): Unit = {
    val provinces = repos.provinces
    val tags = repos.tags

    val eastSlavicPs = provinces.search(ProvinceSearchConf.ofCultureGroup(eastSlavic).all).entities
    val zaporizhia = findAreaProvinces(zaporizhiaArea, repos)
    val muscovy = findProvincesByCore("MOS", provinces)
    val penza = provinces.findByName("Penza").toSeq

    val rusProvinces = (eastSlavicPs ++ zaporizhia ++ muscovy ++ penza).distinct

    val freedRusProvinces = rusProvinces
      .map(minskProvinces)
      .map(turovProvinces)
      .map(ruthenianZaporizhia)
      .map(manualOwner)
      .map(freeSubjugated)
      .map(ruthenianSivershchyna)
      .map(lytvynSmolensk)
      .map(muscoviteRyazan)
      .filterNot(p => p.state.owner.exists(rusSubjugators.contains))

    val freedTags = freedRusProvinces.flatMap(_.history.init.owner).distinct
    logger.info(s"Modded ${freedTags.size} Rus tags: $freedTags")
    logger.info(s"Modded ${freedRusProvinces.size} Rus provinces")

    val QAS = tags.find("QAS").map(tagWithReligion(_, orthodox)).get
    val LIT = tags.find("LIT").map(tagWithReligion(_, orthodox)).get
    val principalities = Seq(LIT, QAS, polotsk, minsk, turov, chernihiv, smolensk, halych, kyiv)
        .map(updateTagHistory(_, tags))

    principalities.foreach(tags.update)
    writeCountries(repos, mod, principalities)

    provinces.update(freedRusProvinces)
    val lietuva = findProvincesByCore("LIT", provinces)
        .map(provinceWithReligion(_, orthodox))
    provinces.update(lietuva)
    writeProvinces(repos, mod, freedRusProvinces ++ lietuva)
  }

  def minskProvinces(p: Province): Province =
    if (p.geography.area.contains(minskArea)) provinceWithOwner(p, "MNK")
    else p

  def turovProvinces(p: Province): Province =
    if (p.geography.area.contains(turovArea)) provinceWithOwner(p, "TRV")
    else p

  def ruthenianZaporizhia(p: Province): Province =
    if (p.geography.area.contains(zaporizhiaArea))
      provinceWithCulture(provinceWithReligion(p, orthodox), ruthenian)
    else p

  def manualOwner(p: Province): Province =
    if (p.localisation.name.exists(coresByProvName.isDefinedAt)) {
      val core = p.localisation.name.flatMap(coresByProvName.get).get
      provinceWithOwner(p, core)
    }
    else p

  def freeSubjugated(p: Province): Province =
    if (p.state.owner.exists(rusSubjugators.contains))
      p.history.init.addCore.getOrElse(Seq.empty)
        .filterNot(rusSubjugators.contains)
        .headOption
        .map(provinceWithOwner(p, _))
        .getOrElse(p)
    else p

  def lytvynSmolensk(p: Province): Province =
    if (p.state.owner.contains("SMO"))
      provinceWithCulture(p, lytvyn)
    else p

  def ruthenianSivershchyna(p: Province): Province =
    if ((p.geography.area.contains(chernigovArea) || p.geography.area.contains(slobodaArea)) && p.state.cultureGroup.contains(eastSlavic))
      provinceWithCulture(p, ruthenian)
    else p

  def muscoviteRyazan(p: Province): Province =
    if (p.localisation.name.exists(muscovitePs.contains))
      provinceWithCulture(p, muscovite)
    else p

  def turov: Tag = {
    rusPrincipality("TRV", "Turov", 1941, lytvyn)
  }

  def minsk: Tag = {
    rusPrincipality("MNK", "Minsk", 276, lytvyn)
  }

  import com.softwaremill.quicklens._

  def smolensk: Tag = {
    val monarch = Monarch(
      adm = 3, dip = 4, mil = 3,
      name = "Izyaslav",
      dynasty = "Rurykovych",
      birthDate = Date(1415, 3, 10)
    )
    val personalities = Seq("just_personality", "tolerant_personality")
    val coronation = TagUpdate(
      date = Date(1434, 2, 27),
      monarch = monarch,
      clearScriptedPersonalities = true,
      addRulerPersonality = personalities
    )
    rusPrincipality("SMO", "Smolensk", 293, lytvyn)
      .modify(_.history.events).setTo(Seq(coronation))
      .modify(_.history.init.historicalRival).setTo(Seq("MOS", "TVE"))
      .modify(_.history.init.historicalFriend).setTo(Seq("CHR"))
  }

  def polotsk: Tag = {
    val monarch = Monarch(
      adm = 5, dip = 3, mil = 3,
      name = "Viacheslav",
      dynasty = "Rurykovych",
      birthDate = Date(1415, 3, 10)
    )
    val personalities = Seq("benevolent_personality", "calm_personality")
    val coronation = TagUpdate(
      date = Date(1434, 2, 27),
      monarch = monarch,
      clearScriptedPersonalities = true,
      addRulerPersonality = personalities
    )
    rusPrincipality("PLT", "Polotsk", 275, lytvyn)
      .modify(_.history.events).setTo(Seq(coronation))
      .modify(_.history.init.historicalRival).setTo(Seq("LIV", "PSK"))
      .modify(_.history.init.historicalFriend).setTo(Seq("LIT"))
  }

  def halych: Tag = {
    val monarch = Monarch(
      adm = 2, dip = 4, mil = 5,
      name = "Danylo III the Brave",
      dynasty = "Rurykovych",
      birthDate = Date(1415, 3, 10)
    )
    val personalities = Seq("conqueror_personality", "inspiring_leader_personality")
    val coronation = TagUpdate(
      date = Date(1434, 2, 27),
      monarch = monarch,
      clearScriptedPersonalities = true,
      addRulerPersonality = personalities
    )
    rusPrincipality("VOL", "Volyn", 279, ruthenian)
      .modify(_.history.events).setTo(Seq(coronation))
      .modify(_.history.init.historicalRival).setTo(Seq("POL", "LIT"))
  }

  def kyiv: Tag = {
    val monarch = Monarch(
      adm = 3, dip = 4, mil = 1,
      name = "Volodymyr",
      dynasty = "Rurykovych",
      birthDate = Date(1415, 3, 10)
    )
    val personalities = Seq("careful_personality", "intricate_web_weaver_personality")
    val coronation = TagUpdate(
      date = Date(1434, 2, 27),
      monarch = monarch,
      clearScriptedPersonalities = true,
      addRulerPersonality = personalities
    )
    rusPrincipality("KIE", "Kyiv", 280, ruthenian)
      .modify(_.history.events).setTo(Seq(coronation))
      .modify(_.history.init.historicalRival).setTo(Seq("CRI", "MOS"))
      .modify(_.history.init.historicalFriend).setTo(Seq("CHR"))
  }

  def chernihiv: Tag = {
    val monarch = Monarch(
      adm = 4, dip = 3, mil = 2,
      name = "Sviatoslav",
      dynasty = "Rurykovych",
      birthDate = Date(1415, 3, 10)
    )
    val personalities = Seq("secretive_personality", "well_connected_personality")
    val coronation = TagUpdate(
      date = Date(1434, 2, 27),
      monarch = monarch,
      clearScriptedPersonalities = true,
      addRulerPersonality = personalities
    )
    rusPrincipality("CHR", "Chernihiv", 280, ruthenian)
      .modify(_.history.events).setTo(Seq(coronation))
      .modify(_.history.init.historicalRival).setTo(Seq("TVE", "CRI"))
      .modify(_.history.init.historicalFriend).setTo(Seq("KIE", "SMO"))
  }

  def rusPrincipality
  (
    id: String,
    name: String,
    capital: Int,
    culture: String,
  ): Tag = {
    val init = TagUpdate(
      government = "monarchy",
      addGovernmentReform = Seq("principality"),
      governmentRank = 1,
      primaryCulture = culture,
      religion = orthodox,
      technologyGroup = "eastern",
      capital = capital,
      fixedCapital = capital,
    )
    val history = TagHistory(init = init)
    val l = Localisation(name = name)
    Tag(id = id, localisation = l, history = history)
  }

  def updateTagHistory(t: Tag, repository: TagRepository): Tag =
    repository
      .find(t.id)
      .map(et => {
        val withSource = et.history.sourceFile.map(sf => t.history.copy(sourceFile = sf)).getOrElse(t.history)
        et.copy(history = withSource)
      })
      .getOrElse(t)


}
