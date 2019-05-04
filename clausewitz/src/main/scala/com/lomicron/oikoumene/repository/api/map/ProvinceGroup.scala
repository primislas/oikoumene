package com.lomicron.oikoumene.repository.api.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.EntityGroup

case class ProvinceGroup
(
  override val value: AnyRef,
  override val entities: Seq[Province] = Seq.empty,
  development: Int = 0
) extends EntityGroup[Province]
