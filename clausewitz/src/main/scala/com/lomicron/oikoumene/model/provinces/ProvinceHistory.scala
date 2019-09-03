package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.parsers.ClausewitzParser.startDate
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class ProvinceHistory
(
  init: ProvinceUpdate = ProvinceUpdate.empty,
  events: Seq[ProvinceUpdate] = Seq.empty,
  state: ProvinceState = ProvinceState.empty,
  sourceFile: Option[String] = None,
) {

  @JsonCreator def this() = this(ProvinceUpdate.empty)

  def withState(state: ProvinceState): ProvinceHistory = copy(state = state)

  def atStart(): ProvinceHistory = at(startDate)

  def atTheEnd(): ProvinceHistory = withState(state.next(events))

  def at(year: Int, month: Int, day: Int): ProvinceHistory = at(Date(year, month, day))

  def at(date: Date): ProvinceHistory = {
    val eventsByDate = Seq(init) ++ events.filter(e => e.date.exists(_ <= date))
    withState(state.next(eventsByDate))
  }

}

object ProvinceHistory extends FromJson[ProvinceHistory] {
  val empty: ProvinceHistory = ProvinceHistory()
}