package com.lomicron.oikoumene.model.politics

import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation

case class CultureGroup
( id: String,
  localisation: Localisation,
  cultureIds: Seq[String],
  graphicalCulture: Option[String],
  maleNames: Seq[String] = Seq.empty,
  femaleNames: Seq[String] = Seq.empty,
  dynastyNames: Seq[String] = Seq.empty
) extends Entity