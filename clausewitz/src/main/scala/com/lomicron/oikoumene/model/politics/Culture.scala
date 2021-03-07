package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.{JsonCreator, JsonProperty}
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.Entity.UNDEFINED
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.JsonMap

@JsonCreator
case class Culture
(id: String = UNDEFINED,
 localisation: Localisation = Localisation.empty,
 cultureGroupId: String = UNDEFINED,
 @JsonProperty("primary") primaryTag: Option[String] = None,
 dynastyNames: Seq[String] = Seq.empty,
 maleNames: Seq[String] = Seq.empty,
 femaleNames: Seq[String] = Seq.empty,
 graphicalCulture: Option[String] = Option.empty,
 country: JsonMap = Map.empty,
 province: JsonMap = Map.empty
) extends Entity {

//  @JsonCreator def this() = this(UNDEFINED)
//  @JsonCreator def this(@JsonProperty("primary") primaryTag: Option[String]) = this(UNDEFINED, primaryTag = primaryTag)

}

object Culture extends FromJson[Culture]
