package services

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.{RepositoryFactory, SearchResult}
import com.lomicron.oikoumene.repository.api.map.{ProvinceGroup, ProvinceSearchConf}
import javax.inject.Inject

class ProvinceService @Inject
(
  repos: RepositoryFactory
) {

  def findProvinces(searchConf: ProvinceSearchConf): SearchResult[Province] =
    repos.provinces.search(searchConf)

  def groupProvinces(searchConf: ProvinceSearchConf, groupBy: String)
  : SearchResult[ProvinceGroup] =
    repos.provinces.groupBy(searchConf, groupBy)

  def getProvince(id: Int): Option[Province] =
    repos.provinces.find(id).toOption

}
