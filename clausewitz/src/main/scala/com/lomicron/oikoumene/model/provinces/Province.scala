package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.parsers.ClausewitzParser.startDate
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class Province
(id: Int,
 color: Color,
 comment: Option[String] = None,
 tag2: Option[String] = None,
 `type`: Option[String] = None,
 localisation: Localisation = Localisation.empty,
 state: ProvinceState = ProvinceState.empty,
 history: Seq[ProvinceUpdate] = Seq.empty,
) { self =>
  @JsonCreator def this() = this(0, Color.black)

  def atStart(): Province = at(startDate)

  def atTheEnd(): Province = copy(state = state.next(history))

  def at(year: Int, month: Int, day: Int): Province = at(Date(year, month, day))

  def at(date: Date): Province = {
      val eventsByDate = history.filter(e => e.date.isEmpty || e.date.exists(_.compareTo(date) <= 0))
      copy(state = state.next(eventsByDate))
  }

}

object Province extends FromJson[Province] {
  def apply(id: Int, color: Color, comment: String):
  Province = Province(id, color, Option(comment).filter(_.nonEmpty))

  def apply(id: Int, color: Color, comment: String, tag2: String): Province =
    Province(
      id,
      color,
      Option(comment).filter(_.nonEmpty),
      Option(tag2).filter(_.nonEmpty))
}