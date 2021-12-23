package services

import com.lomicron.eu4.model.provinces.Province

case class ResProvinceGroupEntity
(id: Int, name: String, owner: Option[String], development: Int = 0)

object ResProvinceGroupEntity {
  def apply(p: Province): ResProvinceGroupEntity = {
    val name = ProvinceService.nameOf(p)
    ResProvinceGroupEntity(p.id, name, p.state.owner, p.state.development)
  }
}
