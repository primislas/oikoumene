package com.lomicron.vicky.model.province

import com.fasterxml.jackson.annotation.{JsonCreator, JsonIgnore}
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.history.HistEvent
import com.lomicron.utils.parsing.tokenizer.Date

@JsonCreator
case class ProvinceUpdate
(
  // hits = 2703, isOptional = true, sample = "629 - Plzen.txt"
  sourceFile: Option[String] = None,
  // hits = 2706, isOptional = true, sample = 35
  lifeRating: Option[Int] = None,
  // hits = 2703, isOptional = true, sample = "coal"
  tradeGoods: Option[String] = None,
  // hits = 2624, isOptional = true, sample = "AUS"
  controller: Option[String] = None,
  // hits = 2624, isOptional = true, sample = "AUS"
  owner: Option[String] = None,
  // hits = 2415, isOptional = true, sample = ["AUS","BOH","CZH"]
  addCore: Option[Seq[String]] = Option.empty,
  // hits = 924, isOptional = true, sample = "1861.1.1"
  override val date: Option[Date] = None,
  // hits = 468, isOptional = true, sample = 1
  railroad: Option[Int] = None,
  // hits = 423, isOptional = true, sample = 2
  colonial: Option[Int] = None,
  // hits = 142, isOptional = true, sample = 1
  navalBase: Option[Int] = None,
  // hits = 115, isOptional = true, sample = {"level":1,"building":"fabric_factory","upgrade":true}
  stateBuilding: Option[Seq[StateBuilding]] = Option.empty,
  // hits = 78, isOptional = true, sample = 2
  colony: Option[Int] = None,
  // hits = 49, isOptional = true, sample = 1
  fort: Option[Int] = None,
  // hits = 20, isOptional = true, sample = {"ideology":"conservative","loyalty_value":10}
  partyLoyalty: Option[PartyLoyalty] = None,
  // hits = 13, isOptional = true, sample = ["ITA","SAR"]
  removeCore: Option[Seq[String]] = Option.empty,
  // hits = 6, isOptional = true, sample = "plains"
  terrain: Option[String] = None,
  // hits = 3, isOptional = true, sample = true
  isSlave: Option[Boolean] = Option.empty,
) extends HistEvent {

  def addCoreCp(core: String): ProvinceUpdate = {
    val cores = addCore.map(_ :+ core).map(_.distinct).orElse(Some(Seq(core)))
    copy(addCore = cores)
  }

  @JsonIgnore def isEmpty: Boolean = !isNotEmpty

  @JsonIgnore def isNotEmpty: Boolean = fields().exists(_.isDefined)

  private def fields(): Seq[Option[Any]] =
    Seq(controller, owner, addCore, removeCore)

}

object ProvinceUpdate {
  def empty: ProvinceUpdate = ProvinceUpdate()
}
