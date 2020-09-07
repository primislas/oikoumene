package com.lomicron.oikoumene.tools.model.metadata

import com.lomicron.oikoumene.model.politics.CultureGroup

case class CultureGroupMetadata
(
  id: String,
  name: Option[String] = None,
  cultures: Seq[String] = Seq.empty,
)

object CultureGroupMetadata {
  def apply(cg: CultureGroup): CultureGroupMetadata =
    CultureGroupMetadata(
      cg.id,
      cg.localisation.name,
      cg.cultureIds.toSeq,
    )
}
