package services

import com.lomicron.oikoumene.model.provinces.Province

case class ResProvinceListEntity
(
  id: Int,
  name: String,
  owner: Option[String] = None,
  cores: Set[String] = Set.empty,

  religion: Option[String] = None,
  culture: Option[String] = None,

  development: Int = 0,
  tradeGood: Option[String] = None,
  tradeNode: Option[String] = None,
)

object ResProvinceListEntity {
  def apply(p: Province): ResProvinceListEntity = {
    val name = ProvinceService.nameOf(p)
    ResProvinceListEntity(p.id, name, p.state.owner, p.state.cores,
      p.state.religion, p.state.culture, p.state.development,
      p.state.tradeGood, p.geography.tradeNode)
  }
}
