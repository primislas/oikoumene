package com.lomicron.oikoumene.model.government

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.utils.json.FromJson

@JsonCreator
case class Idea
(
  // hits = 2823, isOptional = false, sample = "noble_knights"
  id: String = Entity.UNDEFINED,
  // hits = 2804, isOptional = true, sample = {"name":"Noble Knights"}
  localisation: Localisation = Localisation.empty,
  // hits = 2823, isOptional = false, sample = {"cavalry_power":0.10,"cavalry_cost":-0.10}
  modifier: Modifier = Modifier.empty,
) extends Entity

object Idea extends FromJson[Idea]
