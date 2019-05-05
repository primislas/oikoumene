package services

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.map.ProvinceSearchConf
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, SearchResult}
import javax.inject.Inject

class ProvinceService @Inject
(
  repos: RepositoryFactory
) {

  def findProvinces(searchConf: ProvinceSearchConf): SearchResult[ResProvinceListEntity] = {
    val sr = repos.provinces.search(searchConf)
    val es = sr.entities.map(ResProvinceListEntity(_))
    SearchResult(sr.page, sr.size, sr.totalPages, sr.totalEntities, es)
  }

  def groupProvinces(searchConf: ProvinceSearchConf, groupBy: String)
  : SearchResult[ResProvinceGroup] = {
    val res = repos.provinces.groupBy(searchConf, groupBy)
    val trimmed = res.entities.map(ResProvinceGroup(_))
    SearchResult(res.page, res.size, res.totalPages, res.totalEntities, trimmed)
  }

  def getProvince(id: Int): Option[Province] =
    repos.provinces.find(id).toOption

}

object ProvinceService {

  def nameOf(p: Province): String =
    p.localisation.name.orElse(p.comment).getOrElse(s"PROV${p.id}")

}