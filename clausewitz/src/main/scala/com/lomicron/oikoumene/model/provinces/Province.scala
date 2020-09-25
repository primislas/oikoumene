package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.localisation.{Localisation, WithLocalisation}
import com.lomicron.oikoumene.parsers.ClausewitzParser.startDate
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class Province
(id: Int,
 color: Color,
 comment: Option[String] = None,
 tag2: Option[String] = None,
 localisation: Localisation = Localisation.empty,
 history: ProvinceHistory = ProvinceHistory.empty,
 geography: ProvinceGeography = ProvinceGeography.empty,
) extends WithLocalisation {
  self =>
  @JsonCreator def this() = this(0, Color.black)

  def state: ProvinceState = history.state

  def withState(state: ProvinceState): Province = copy(history = history.withState(state))

  def atStart: Province = at(startDate)

  def atTheEnd: Province = copy(history = history.atTheEnd())

  def at(year: Int, month: Int, day: Int): Province = at(Date(year, month, day))

  def at(date: Date): Province = {
    copy(history = history.at(date))
  }

  def withTradeNode(tnId: String): Province =
    copy(geography = geography.copy(tradeNode = Option(tnId)))

  def withTerrain(t: String): Province =
    copy(geography = geography.copy(terrain = Option(t)))

  def updateInitState(f: ProvinceUpdate => ProvinceUpdate): Province = {
    val initState = f(history.init)
    val withoutDate =
      if (initState.date.isEmpty) initState
      else initState.copy(date = Option.empty)
    val h = history.copy(init = withoutDate)
    copy(history = h)
  }

  def `type`: String = {
    if (geography.isImpassable) ProvinceTypes.wasteland
    else if (geography.provinceType == ProvinceTypes.province && state.owner.isEmpty)
      ProvinceTypes.uncolonized
    else geography.provinceType
  }

  def isLand: Boolean = ProvinceTypes.landTypes.contains(`type`)

}

object Province extends FromJson[Province] {
  def apply(id: Int, color: Color, comment: String): Province =
    Province(id, color, Option(comment).filter(_.nonEmpty))

  def apply(id: Int, color: Color, comment: String, tag2: String): Province =
    Province(
      id,
      color,
      Option(comment).filter(_.nonEmpty),
      Option(tag2).filter(_.nonEmpty))
}
