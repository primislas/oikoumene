package com.lomicron.eu4.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

@JsonCreator
case class Area
(// hits = 831, isOptional = false, sample = "adamawa_plateau_area"
 id: String = Entity.UNDEFINED,
 // hits = 831, isOptional = false, sample = {"name":"Adamawa Plateau"}
 localisation: Localisation = Localisation.empty,
 // hits = 831, isOptional = false, sample = [1161,1162,1249,2285]
 provinceIds: Seq[Int] = Seq.empty,
 // hits = 46, isOptional = true, sample = [92,200,92]
 color: Option[Color] = None,
) extends Entity

object Area extends FromJson[Area]
