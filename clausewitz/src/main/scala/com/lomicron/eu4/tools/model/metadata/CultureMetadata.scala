package com.lomicron.eu4.tools.model.metadata

import com.lomicron.eu4.model.politics.Culture

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
