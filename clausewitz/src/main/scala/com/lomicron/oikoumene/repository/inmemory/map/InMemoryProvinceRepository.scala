package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.map._
import com.lomicron.oikoumene.repository.api.{SearchConf, SearchResult}
import com.lomicron.oikoumene.repository.inmemory.InMemoryIntRepository
import com.lomicron.oikoumene.service.NamingService
import com.lomicron.utils.collection.CollectionUtils._

case class InMemoryProvinceRepository()
  extends InMemoryIntRepository[Province](p => Option(p.id))
    with ProvinceRepository {

  override def setId(entity: Province, id: Int): Province =
    entity.copy(id = id)

  override def search(req: SearchConf): SearchResult[Province] = {
    req match {
      case conf: ProvinceSearchConf => search(conf)
      case _ => super.search(req)
    }
  }

  def search(req: ProvinceSearchConf): SearchResult[Province] = {
    val withoutName = findAll
      .filter(p => searchArgMatches(req.owner, p.state.owner))
      .filter(p => searchArgMatches(req.controller, p.state.controller))

      .filter(p => searchArgMatches(req.religion, p.state.religion))
      .filter(p => searchArgMatches(req.religionGroup, p.state.religionGroup))
      .filter(p => searchArgMatches(req.culture, p.state.culture))
      .filter(p => searchArgMatches(req.cultureGroup, p.state.cultureGroup))

      .filter(p => searchArgMatches(req.area, p.geography.area))
      .filter(p => searchArgMatches(req.region, p.geography.region))
      .filter(p => searchArgMatches(req.superRegion, p.geography.superRegion))
      .filter(p => searchArgMatches(req.continent, p.geography.continent))

      .filter(p => searchArgMatches(req.tradeGood, p.state.tradeGood))
      .filter(p => searchArgMatches(req.tradeNode, p.geography.tradeNode))

      .filter(p => req.core.isEmpty || req.core.exists(p.state.cores.contains))

    val allMatching = req.name.map(n => NamingService.makeAliases(n))
      .map(ns => withoutName.filter(p => ns.exists(n => p.localisation.matches(n))))
      .getOrElse(withoutName)

    val quotient = allMatching.size / req.size
    val rem = allMatching.size % req.size
    val matchingPages = if (rem > 0) quotient + 1 else quotient
    val page = if (req.offset < allMatching.size) req.page else 1
    val offset = (page - 1) * req.size
    val provinces = allMatching
      .slice(offset, offset + req.size)
      .map(p => if (req.excludeFields.nonEmpty) excludeFields(p, req.excludeFields) else p)

    SearchResult(page, req.size, matchingPages, allMatching.size, provinces)
  }

  override def groupBy(searchConf: ProvinceSearchConf, group: String)
  : SearchResult[ProvinceGroup] = {
    val ps = search(searchConf.copy(size = Int.MaxValue)).entities
    val page = searchConf.page
    val size = searchConf.size

    def closedGroupBy(f: Province => AnyRef): SearchResult[ProvinceGroup] =
      groupBy(ps, f, page, size, searchConf.excludeFields, searchConf.includeFields)

    group match {
      case ProvinceFields.tag => closedGroupBy(ProvinceFields.tagOf)
      // TODO to group by core we should return as many entities as there're cores
      //      case ProvinceFields.core => closedGroupBy(ProvinceFields.coreOf)

      case ProvinceFields.religion => closedGroupBy(ProvinceFields.religionOf)
      case ProvinceFields.religionGroup => closedGroupBy(ProvinceFields.areaOf)
      case ProvinceFields.culture => closedGroupBy(ProvinceFields.cultureOf)
      case ProvinceFields.cultureGroup => closedGroupBy(ProvinceFields.cultureGroupOf)

      case ProvinceFields.area => closedGroupBy(ProvinceFields.areaOf)
      case ProvinceFields.region => closedGroupBy(ProvinceFields.regionOf)
      case ProvinceFields.superregion => closedGroupBy(ProvinceFields.superregionOf)
      case ProvinceFields.continent => closedGroupBy(ProvinceFields.continentOf)

      case ProvinceFields.tradeGood => closedGroupBy(ProvinceFields.tradeGoodOf)
      case ProvinceFields.tradeNode => closedGroupBy(ProvinceFields.tradeNodeOf)

      case _ => SearchResult(page, size, 0, 0, Seq(ProvinceGroup("UNDEFINED")))
    }

  }

  private def groupBy
  (
    ps: Seq[Province],
    f: Province => AnyRef,
    page: Int = 0,
    size: Int = 10,
    excludedFields: Set[String] = Set.empty,
    includedFields: Set[String] = Set.empty,
  ): SearchResult[ProvinceGroup] = {

    val offset = page * size
    val groups = ps
      .groupBy(f)
      .mapKVtoValue(groupProvinces)
      .values.toSeq
      .sortBy(-_.development)

    val groupPage = groups
      .slice(offset, offset + size)
      .map(pg => if (excludedFields.nonEmpty) pg.copy(entities = pg.entities.map(excludeFields(_, excludedFields))) else pg)

    SearchResult(page, size, groups.size, groupPage)
  }

  private def groupProvinces(v: AnyRef, ps: Seq[Province]): ProvinceGroup = {
    val development = ps.foldLeft(0)(_ + _.state.development)
    ProvinceGroup(v, ps, development)
  }

  private def excludeFields(p: Province, fs: Set[String]): Province = {
    if (fs.isEmpty) p
    else {
      if (fs.contains(ProvinceFields.history)) p.copy(history = Seq.empty)
      else p
    }
  }

}
