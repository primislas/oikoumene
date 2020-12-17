package com.lomicron.oikoumene.repository.api.government

import com.lomicron.oikoumene.model.government.{Government, LegacyGovernmentMapping}
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait GovernmentRepository extends AbstractRepository[String, Government] {
  def ids: Set[String] = {
    val gs = findAll
    val gids = gs.map(_.id)
    val legacyGids = gs.flatMap(_.legacyGovernment)
    gids.toSet ++ legacyGids
  }
  def legacyMapping(mapping: Map[String, LegacyGovernmentMapping]): GovernmentRepository
  def legacyMapping: Map[String, LegacyGovernmentMapping]
}
