package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.{AbstractRepository, SearchResult}

trait ProvinceRepository extends AbstractRepository[Int, Province] {

  def groupBy(searchConf: ProvinceSearchConf, groupBy: String): SearchResult[ProvinceGroup]
  def findByName(name: String): Option[Province]
  def findByName(names: Seq[String]): Seq[Province]
  def findByColor(c: Color): Option[Province]
  def findByColor(c: Int): Option[Province]

}
