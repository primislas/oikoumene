package com.lomicron.oikoumene.tools.model.metadata

import com.lomicron.oikoumene.model.politics.Culture

case class CultureMetadata
(
  id: String,
  name: Option[String] = None,
  cultureGroup: Option[String] = None,
)

object CultureMetadata {
  def apply(c: Culture): CultureMetadata =
    CultureMetadata(c.id, c.localisation.name, Option(c.cultureGroupId))
}
