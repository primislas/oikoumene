package services

import com.lomicron.eu4.repository.api.map.ProvinceGroup
import com.lomicron.oikoumene.repository.api.EntityGroup

case class ResProvinceGroup
(override val value: AnyRef, development: Int = 0, override val entities: Seq[ResProvinceGroupEntity] = Seq.empty)
extends EntityGroup[ResProvinceGroupEntity]

object ResProvinceGroup {
  def apply(g: ProvinceGroup): ResProvinceGroup = {
    val es = g.entities.map(ResProvinceGroupEntity(_))
    ResProvinceGroup(g.value, g.development, es)
  }
}
