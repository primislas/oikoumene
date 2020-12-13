package com.lomicron.oikoumene.repository.api.government

import com.lomicron.oikoumene.model.government.{Government, LegacyGovernmentMapping}
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait GovernmentRepository extends AbstractRepository[String, Government] {
  def legacyMapping(mapping: Map[String, LegacyGovernmentMapping]): GovernmentRepository
  def legacyMapping: Map[String, LegacyGovernmentMapping]
}
