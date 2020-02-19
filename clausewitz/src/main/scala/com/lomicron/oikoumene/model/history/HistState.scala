package com.lomicron.oikoumene.model.history

trait HistState[D <: HistState[D, E], E <: HistEvent] {
  def next(event: E): D
  def next(events: Seq[E]): D
}
