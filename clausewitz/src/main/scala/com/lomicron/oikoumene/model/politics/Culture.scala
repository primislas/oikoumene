package com.lomicron.oikoumene.model.politics

import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation

case class Culture
(id: String,
 localisation: Localisation,
 cultureGroupId: String,
 primaryTag: String,
 dynastyNames: Seq[String] = Seq.empty,
 maleNames: Seq[String] = Seq.empty,
 femaleNames: Seq[String] = Seq.empty,
 graphicalCulture: Option[String] = Option.empty,
 country: Map[String, AnyRef] = Map.empty,
 province: Map[String, AnyRef] = Map.empty
) extends Entity