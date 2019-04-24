package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class ProvinceHistory
(state: ProvinceState = ProvinceState.empty,
 events: Seq[ProvinceUpdate] = Seq.empty) {

  @JsonCreator def this() = this(ProvinceState.empty)

  def atTheEnd(): ProvinceState =
    state.next(events)

  def at(year: Int, month: Int, day: Int): ProvinceState = at(Date(year, month, day))

  def at(date: Date): ProvinceState = {
    val eventsByDate = events
      .filter(e => e.date.isEmpty || e.date.exists(_.compareTo(date) <= 0))
    state.next(eventsByDate)
  }

}

object ProvinceHistory extends FromJson[ProvinceHistory]