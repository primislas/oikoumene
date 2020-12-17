package com.lomicron.oikoumene.repository.api.government

import com.lomicron.oikoumene.model.government.{TechLevel, Technology}
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TechnologyRepository extends AbstractRepository[String, Technology] {

  def groups: TechnologyGroupRepository

  def getTech(id: String, level: Int): Option[TechLevel] =
    find(id).toOption
      .filter(_.levels.length > level)
      .map(_.levels(level))

}
