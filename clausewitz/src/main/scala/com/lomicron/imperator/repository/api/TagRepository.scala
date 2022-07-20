package com.lomicron.imperator.repository.api

import com.lomicron.imperator.model.politics.Tag
import com.lomicron.oikoumene.repository.api.AbstractRepository

trait TagRepository extends AbstractRepository[String, Tag] {
  def ownerOfProvince(provId: Int): Option[Tag]
  def distinctProvinceOwners: Seq[Tag]
}