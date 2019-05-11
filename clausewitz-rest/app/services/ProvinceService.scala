package services

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.map.ProvinceSearchConf
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, SearchDictionary, SearchResult}
import javax.inject.Inject

class ProvinceService @Inject
(
  repos: RepositoryFactory
) {

  def findProvinces(searchConf: ProvinceSearchConf): SearchResult[ResProvinceListEntity] = {
    val sr = repos.provinces.search(searchConf)
    val es = sr.entities.map(ResProvinceListEntity(_))
    val dict = if (searchConf.withDictionary) buildDictionary(searchConf, es) else SearchDictionary.empty
    SearchResult(sr.page, sr.size, sr.totalPages, sr.totalEntities, es, dict)
  }

  def groupProvinces(searchConf: ProvinceSearchConf, groupBy: String)
  : SearchResult[ResProvinceGroup] = {
    val res = repos.provinces.groupBy(searchConf, groupBy)
    val trimmed = res.entities.map(ResProvinceGroup(_))
    SearchResult(res.page, res.size, res.totalPages, res.totalEntities, trimmed)
  }

  def getProvince(id: Int): Option[Province] =
    repos.provinces.find(id).toOption

  def buildDictionary(conf: ProvinceSearchConf, entities: Seq[ResProvinceListEntity]): SearchDictionary = {
    val tags = conf.owner.toSeq ++ conf.core.toSeq ++ entities.flatMap(e => e.owner.toSeq ++ e.cores)
    val tagNames = repos.tags.findNames(tags.distinct)

    val religions = (conf.religion.toSeq ++ entities.flatMap(_.religion)).distinct
    val religionNames = repos.religions.findNames(religions)

    val cultures = (conf.culture.toSeq ++ entities.flatMap(_.culture)).distinct
    val cultureNames = repos.cultures.findNames(cultures)

    val tradeGoods = (conf.tradeGood.toSeq ++ entities.flatMap(_.tradeGood)).distinct
    val trGoodNames = repos.tradeGoods.findNames(tradeGoods)



    SearchDictionary.empty.copy(
      tag = tagNames,
      religion = religionNames,
      culture = cultureNames,
      tradeGood = trGoodNames)
  }

}

object ProvinceService {

  def nameOf(p: Province): String =
    p.localisation.name.orElse(p.comment).getOrElse(s"PROV${p.id}")

}
