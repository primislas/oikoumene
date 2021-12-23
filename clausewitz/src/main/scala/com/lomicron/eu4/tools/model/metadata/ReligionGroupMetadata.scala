package com.lomicron.eu4.tools.model.metadata

import com.lomicron.eu4.model.politics.ReligionGroup
import com.lomicron.oikoumene.model.{Color, Entity}

case class ReligionGroupMetadata
(
  id: String = Entity.UNDEFINED,
  name: Option[String] = None,
  color: Option[Color] = None,
  religions: Seq[String] = Seq.empty,
)

object ReligionGroupMetadata {
  def apply(rg: ReligionGroup): ReligionGroupMetadata =
    ReligionGroupMetadata(rg.id, rg.localisation.name)
}
