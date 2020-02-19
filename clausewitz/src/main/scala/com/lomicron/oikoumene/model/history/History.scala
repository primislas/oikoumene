package com.lomicron.oikoumene.model.history

import com.lomicron.utils.parsing.tokenizer.Date

trait History[D <: History[D, S, E], S <: HistState[S, E], E <: HistEvent] {
  def init: E
  def state: S
  val events: Seq[E] = Seq.empty
  val sourceFile: Option[String] = Option.empty

  def withState(state: S): D

  def atStart(): D = at(Int.MinValue, 0, 0)

  def atTheEnd(): D = withState(state.next(events))

  def at(year: Int, month: Int, day: Int): D = at(Date(year, month, day))

  def at(date: Date): D = {
    val eventsByDate = Seq(init) ++ events.filter(e => e.date.exists(_ <= date))
    withState(state.next(eventsByDate))
  }

}
