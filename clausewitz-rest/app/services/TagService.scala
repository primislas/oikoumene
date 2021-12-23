package services

import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.repository.api.map.ProvinceSearchConf
import com.lomicron.eu4.repository.api.politics.TagSearchConf
import com.lomicron.eu4.repository.api.RepositoryFactory
import com.lomicron.eu4.repository.api.search.SearchDictionary
import com.lomicron.oikoumene.repository.api.search
import com.lomicron.oikoumene.repository.api.search.SearchResult

import javax.inject.Inject

class TagService @Inject
(
  repos: RepositoryFactory
) {

  def findTags(searchConf: TagSearchConf): SearchResult[ResTagListEntity] = {
    val sr = repos.tags.search(searchConf)
    val es = sr.entities.map(ResTagListEntity(_))
    val dict = if (searchConf.withDictionary) buildDictionary(searchConf, es) else SearchDictionary.empty
    val res = search.SearchResult(sr.page, sr.size, sr.totalPages, sr.totalEntities, es, dict)
    val withProvinces = res.entities.map(tag => {
      val ps = repos.provinces.search(ProvinceSearchConf().copy(size = repos.provinces.size, owner = Option(tag.id))).entities
      val dev = ps.map(_.state.development).sum
      tag.copy(provinces = Option(ps.size), development = Option(dev))
    })

    res.copy(entities = withProvinces)
  }

  def getTag(id: String): Option[Tag] =
    repos.tags.find(id)

  def buildDictionary(conf: TagSearchConf, entities: Seq[ResTagListEntity]): SearchDictionary = {

    val religions = entities.flatMap(_.religion).distinct
    val religionNames = repos.religions.findNames(religions)

    val cultures = (conf.primaryCulture.toSeq ++ entities.flatMap(_.primaryCulture)).distinct
    val cultureNames = repos.cultures.findNames(cultures)

    SearchDictionary.empty.copy(
      religion = religionNames,
      culture = cultureNames
    )
  }

}

case class ResTagListEntity
(
  id: String,
  name: String,
  religion: Option[String] = None,
  primaryCulture: Option[String] = None,

  development: Option[Int] = None,
  provinces: Option[Int] = None,
)

object ResTagListEntity {
  def apply(tag: Tag): ResTagListEntity = {
      ResTagListEntity(
        tag.id,
        tag.localisation.name.getOrElse(tag.id),
        tag.state.religion,
        tag.state.primaryCulture
      )
  }
}
