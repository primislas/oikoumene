package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class CultureGroup
(id: String = UNDEFINED,
 localisation: Localisation = Localisation.empty,
 cultureIds: Seq[String] = Seq.empty,
 graphicalCulture: Option[String] = None,
 maleNames: Seq[String] = Seq.empty,
 femaleNames: Seq[String] = Seq.empty,
 dynastyNames: Seq[String] = Seq.empty
) extends Entity {

  @JsonCreator def this() = this(UNDEFINED)

}

object CultureGroup extends FromJson[CultureGroup]