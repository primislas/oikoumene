package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class SuperRegion
(// hits = 22, isOptional = false, sample = "africa_superregion"
 id: String = Entity.UNDEFINED,
 // hits = 22, isOptional = false, sample = ["niger_region","guinea_region","central_africa_region","sahel_region","horn_of_africa_region","east_africa_region","kongo_region","south_africa_region","maghreb_region","egypt_region"]
 regionIds: Seq[String] = Seq.empty,
 // hits = 14, isOptional = true, sample = {"name":"Africa"}
 localisation: Localisation = Localisation.empty,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object SuperRegion extends FromJson[SuperRegion]
