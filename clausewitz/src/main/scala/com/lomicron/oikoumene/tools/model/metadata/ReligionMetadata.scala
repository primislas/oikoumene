package com.lomicron.oikoumene.tools.model.metadata

import com.lomicron.oikoumene.model.politics.Religion
import com.lomicron.oikoumene.model.{Color, Entity}

case class ReligionMetadata
(
  id: String = Entity.UNDEFINED,
  name: Option[String] = None,
  color: Option[Color] = None,
  religionGroup: Option[String] = None,
)

object ReligionMetadata {
  def apply(religion: Religion): ReligionMetadata =
    ReligionMetadata(
      religion.id,
      religion.localisation.name,
      Option(religion.color),
      Option(religion.religionGroup),
    )
}
