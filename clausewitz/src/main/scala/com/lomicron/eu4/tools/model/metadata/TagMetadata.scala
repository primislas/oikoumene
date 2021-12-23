package com.lomicron.eu4.tools.model.metadata

import com.lomicron.eu4.model.politics.{Monarch, Tag}
import com.lomicron.oikoumene.model.Color

case class TagMetadata
(
  id: String,
  name: Option[String] = None,
  color: Option[Color] = None,

  capital: Option[Int] = None,
  government: Option[String] = None,
  governmentRank: Option[Int] = None,
  technologyGroup: Option[String] = None,
  monarch: Option[Monarch] = None,

  primaryCulture: Option[String] = None,
  acceptedCultures: Seq[String] = Seq.empty,
  religion: Option[String] = None,
)

object TagMetadata {
  def apply(t: Tag): TagMetadata =
    TagMetadata(
      t.id,
      t.localisation.name,
      Option(t.color),

      t.state.capital,
      t.state.government,
      t.state.governmentRank,
      Option(t.state.technologyGroup),
      t.state.monarch,

      t.state.primaryCulture,
      t.state.acceptedCultures,
      t.state.religion
    )
}
