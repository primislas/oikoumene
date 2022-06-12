package com.lomicron.imperator.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

case class Tag
(
  // hits = 598, isOptional = false, sample = "ABC"
  id: String = Entity.UNDEFINED,
  // hits = 593, isOptional = true, sample = {"name":"Albicia"}
  localisation: Localisation = Localisation.empty,
  // hits = 598, isOptional = false, sample = [133,133,20]
  color: Color = Color.black,
  state: Option[TagUpdate] = None,
  // hits = 590, isOptional = true, sample = ["Anderiton","Anicio","Bannaciacon","Bibracte","Bormo","Borvo","Briva","Gabales","Gergovia","Icioduron","Iciomagos","Mediocantos","Randosatis","Rigomagos","Rodumna","Ruessio","Segeta","Segodunon","Segusiaves","Sitillia","Tritullos","Ubrivon","Vellaves","Vorogio"]
  shipNames: Seq[String] = Seq.empty,
  // hits = 339, isOptional = true, sample = [255,223,0]
  color2: Color = Color.black,
  // hits = 221, isOptional = true, sample = false
  genderEquality: Option[Boolean] = None,
  // hits = 4, isOptional = true, sample = "barbarian_gfx"
  graphicalCulture: Option[String] = None,
  // hits = 4, isOptional = true, sample = "barbarians"
  `type`: Option[String] = None,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
  def withId(id: String): Tag = copy(id = id)
  def withState(state: TagUpdate): Tag = copy(state = Some(state))
}

object Tag extends FromJson[Tag]
