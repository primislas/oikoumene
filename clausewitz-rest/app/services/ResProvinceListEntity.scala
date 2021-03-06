package services

import com.lomicron.oikoumene.model.provinces.Province

import scala.collection.immutable.SortedSet

case class ResProvinceListEntity
(
  id: Int,
  name: String,
  owner: Option[String] = None,
  cores: Seq[String] = Seq.empty,

  religion: Option[String] = None,
  culture: Option[String] = None,

  development: Int = 0,
  tradeGood: Option[String] = None,
  tradeNode: Option[String] = None,
)

object ResProvinceListEntity {
  def apply(p: Province): ResProvinceListEntity = {
    val name = ProvinceService.nameOf(p)
    val ownerCore = p.state.cores.find(p.state.owner.contains(_)).toSeq
    val sortedCores = SortedSet[String]() ++ p.state.cores.filter(!p.state.owner.contains(_))
    val cores = ownerCore ++ sortedCores
    ResProvinceListEntity(p.id, name, p.state.owner, cores,
      p.state.religion, p.state.culture, p.state.development,
      p.state.tradeGood, p.geography.tradeNode)
  }
}
