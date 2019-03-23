package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.EntityState
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.parsing.tokenizer.Date

case class ProvinceHistory
(state: ProvinceState = ProvinceState.empty,
 events: Seq[ProvinceUpdate] = Seq.empty)
{

  @JsonCreator def this() = this(ProvinceState.empty)

  def atTheEnd(): EntityState[ProvinceState, ProvinceUpdate] =
    state.next(events)

  def at(date: Date): EntityState[ProvinceState, ProvinceUpdate] = {
    val eventsByDate = events
      .filter(e => e.date.isEmpty || e.date.exists(_.compareTo(date) <= 0))
    state.next(eventsByDate)
  }

}

object ProvinceHistory extends FromJson[ProvinceHistory]