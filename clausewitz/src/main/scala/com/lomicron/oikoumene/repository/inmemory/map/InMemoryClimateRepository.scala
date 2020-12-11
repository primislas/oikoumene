package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.provinces.Climate
import com.lomicron.oikoumene.repository.api.map.ClimateRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryEntityRepository

object InMemoryClimateRepository
  extends InMemoryEntityRepository[Climate]
    with ClimateRepository { self =>

  private var equatorYProv: Option[Int] = None

  override def setId(entity: Climate, id: String): Climate = entity.copy(id = id)

  override def equatorYOnProvinceImage(provId: Int): ClimateRepository = {
    equatorYProv = Option(provId)
    self
  }

  override def equatorYOnProvinceImage: Option[Int] = equatorYProv
}
