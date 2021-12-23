package com.lomicron.eu4.repository.api.government

import com.lomicron.eu4.model.government.{Government, LegacyGovernmentMapping}
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
