package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.{AbstractRepository, SearchResult}

trait ProvinceRepository extends AbstractRepository[Int, Province] {

  def groupBy(searchConf: ProvinceSearchConf, groupBy: String): SearchResult[ProvinceGroup]

}
