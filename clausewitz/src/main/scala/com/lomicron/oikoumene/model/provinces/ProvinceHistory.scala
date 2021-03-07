package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.history.History
import com.lomicron.utils.json.FromJson

@JsonCreator
case class ProvinceHistory
(
  override val init: ProvinceUpdate = ProvinceUpdate.empty,
  override val events: Seq[ProvinceUpdate] = Seq.empty,
  override val state: ProvinceState = ProvinceState.empty,
  override val sourceFile: Option[String] = None,
) extends History[ProvinceHistory, ProvinceState, ProvinceUpdate] {

  def withState(state: ProvinceState): ProvinceHistory = copy(state = state)

  override def addEvent(e: ProvinceUpdate): ProvinceHistory = copy(events = events :+ e)
}

object ProvinceHistory extends FromJson[ProvinceHistory] {
  val empty: ProvinceHistory = ProvinceHistory()
}
