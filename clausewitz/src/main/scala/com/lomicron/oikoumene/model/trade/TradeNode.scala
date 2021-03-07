package com.lomicron.oikoumene.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

@JsonCreator
case class TradeNode
(
  // hits = 77, isOptional = false, sample = "african_great_lakes"
  id: String = Entity.UNDEFINED,
  // hits = 77, isOptional = false, sample = {"name":"Great Lakes"}
  localisation: Localisation = Localisation.empty,
  // hits = 77, isOptional = false, sample = "00_tradenodes.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 77, isOptional = false, sample = 4064
  location: Int = 0,
  // hits = 77, isOptional = false, sample = [1273,1649,1798,1799,1925,4053,4054,4055,4056,4057,4058,4059,4060,4061,4062,4063,4064,4065,4066,4067,4068,4069,4070,4071,4072,4073,4074,4075,4076,4077]
  members: ListSet[Int] = ListSet.empty,
  // hits = 74, isOptional = true, sample = [{"name":"zanzibar","path":[1273,1202],"control":[3351.000000,607.000000,3388.000000,610.000000,3416.000000,580.000000]},{"name":"kongo","path":[4063,4070,4099],"control":[3299.000000,628.000000,3287.000000,595.000000,3270.000000,574.000000,3260.000000,559.000000,3231.000000,537.000000]}]
  outgoing: Seq[TradeNodeRoute] = Seq.empty,
  // hits = 39, isOptional = true, sample = [57,168,220]
  color: Option[Color] = None,
  // hits = 26, isOptional = true, sample = true
  inland: Boolean = false,
  // hits = 3, isOptional = true, sample = true
  aiWillPropagateThroughTrade: Boolean = false,
  // hits = 3, isOptional = true, sample = true
  end: Boolean = false,
) extends Entity

object TradeNode extends FromJson[TradeNode]
