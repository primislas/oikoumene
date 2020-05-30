package com.lomicron.oikoumene.mods

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.RepositoryFactory

object IndependentBalkans {

  def apply(repos: RepositoryFactory): Unit = {
    val provinces = repos.provinces

    val balkans = ModUtils.findProvincesByRegion("balkan_region", repos)
    val transylvania = ModUtils.findProvincesByCore("TRA", provinces)
    val slovakia = ModUtils.findProvincesByCulture("slovak", provinces)
    val balkanProvinces = balkans ++ transylvania ++ slovakia

    val balkanSubjugators = Set("HUN", "TUR")

    def isOwnedByPrimaryCulture(p: Province): Boolean = {
      val provCulture = p.state.culture
      val tagCulture = ModUtils.findOwnerTag(p, repos).flatMap(_.state.primaryCulture)
      val isMatch = for {pc <- provCulture; tc <- tagCulture} yield pc == tc
      isMatch.contains(true)
    }

    def isOwnedBySubjugator(p: Province) = p.state.owner.exists(balkanSubjugators.contains)

    def isSubjugated(p: Province) = !isOwnedByPrimaryCulture(p) && isOwnedBySubjugator(p)

    val freedBalkanProvinces = balkanProvinces
      .filter(isSubjugated)
      .filterNot(p => p.state.culture.contains("greek") && !p.state.owner.contains("TUR"))
      .flatMap(p => {
        ModUtils.findProvPrimaryTag(p, repos)
          .map(_.id)
          .orElse(p.state.cores.find(t => !p.state.owner.contains(t)))
          .orElse(if (p.state.culture.contains("transylvanian")) Option("TRA") else None)
          .map(ModUtils.provinceWithOwner(p, _))
      })
      .map(p => if (p.state.owner.contains("TRA")) ModUtils.provinceWithReligion(p, "orthodox") else p)
      .map(ModUtils.removeDhimmiEstate)

    val transylvaniaTag = ModUtils.findTagByName("Transylvania", repos)
      .map(ModUtils.tagWithReligion(_, "orthodox")).get

    val modDir = "balkans"
    repos.tags.update(transylvaniaTag)
    provinces.update(freedBalkanProvinces)
    ModUtils.writeProvinces(repos, modDir, freedBalkanProvinces)
    ModUtils.writeCountries(repos, modDir, Seq(transylvaniaTag))
  }

}
