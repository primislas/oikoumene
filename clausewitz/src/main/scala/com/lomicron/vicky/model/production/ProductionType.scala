package com.lomicron.vicky.model.production

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListMap

case class ProductionType
(
  // hits = 84, isOptional = false, sample = "factory_template"
  id: String = Entity.UNDEFINED,
  // hits = 30, isOptional = true, sample = {"name":"Aeroplane Factory"}
  localisation: Localisation = Localisation.empty,
  // hits = 78, isOptional = true, sample = "aeroplanes"
  outputGoods: Option[String] = None,
  // hits = 78, isOptional = true, sample = 0.91
  value: Option[BigDecimal] = None,
  // hits = 58, isOptional = true, sample = {"machine_parts":1.11,"electric_gear":2,"rubber":1,"lumber":3.3}
  inputGoods: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 50, isOptional = true, sample = "machine_part_user_template"
  template: Option[String] = None,
  // hits = 34, isOptional = true, sample = {"poptype":"capitalists","effect":"input","effect_multiplier":-2.5}
  owner: Option[ProductionPopConf] = None,
  // hits = 34, isOptional = true, sample = "factory"
  `type`: Option[String] = None,
  // hits = 34, isOptional = true, sample = 10000
  workforce: Option[Int] = None,
  // hits = 30, isOptional = true, sample = [{"trigger":{"has_building":"machine_parts_factory"},"value":0.25},{"trigger":{"has_building":"electric_gear_factory"},"value":0.25}]
  bonus: Seq[ProductionPopConf] = Seq.empty,
  // hits = 15, isOptional = true, sample = true
  farm: Boolean = false,
  // hits = 6, isOptional = true, sample = [{"poptype":"craftsmen","effect":"throughput","amount":0.8},{"poptype":"clerks","effect":"output","effect_multiplier":1.5,"amount":0.2}]
  employees: Seq[ObjectNode] = Seq.empty,
  // hits = 5, isOptional = true, sample = true
  mine: Boolean = false,
  // hits = 4, isOptional = true, sample = {"cement":0.5,"machine_parts":0.05}
  efficiency: ListMap[String, BigDecimal] = ListMap.empty,
  // hits = 4, isOptional = true, sample = true
  isCoastal: Boolean = false,
) extends Entity {
  @JsonCreator def this() = this(Entity.UNDEFINED)
}

object ProductionType extends FromJson[ProductionType]
