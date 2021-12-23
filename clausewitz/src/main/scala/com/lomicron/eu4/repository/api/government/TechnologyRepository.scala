package com.lomicron.eu4.repository.api.government

import com.lomicron.eu4.model.government.{TechLevel, Technology}
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TechnologyRepository extends AbstractRepository[String, Technology] {

  def groups: TechnologyGroupRepository

  def getTech(id: String, level: Int): Option[TechLevel] =
    find(id)
      .filter(_.levels.length > level)
      .map(_.levels(level))

}
