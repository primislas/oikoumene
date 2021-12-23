package com.lomicron.eu4.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

@JsonCreator
case class Region
(// hits = 92, isOptional = false, sample = "american_east_coast_region"
 id: String = Entity.UNDEFINED,
 // hits = 92, isOptional = false, sample = ["sea_of_labrador_area","hudson_bay_sea_area","gulf_of_st_lawrence_area","gulf_stream_area"]
 areas: Seq[String] = Seq.empty,
 // hits = 92, isOptional = false, sample = {"name":"American East Coast"}
 localisation: Localisation = Localisation.empty,
 // hits = 31, isOptional = true, sample = ["0.10.1","00.12.30","0.1.1","00.03.30"]
 monsoon: Seq[Seq[String]] = Seq.empty,
) extends Entity

object Region extends FromJson[Region]
