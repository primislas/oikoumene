package com.lomicron.eu4.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.eu4.model.history.History
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TagHistory
(
  override val init: TagUpdate = TagUpdate.empty,
  override val events: Seq[TagUpdate] = Seq.empty,
  override val state: TagState = TagState.empty,
  override val sourceFile: Option[String] = None,

) extends History[TagHistory, TagState, TagUpdate] {

  override def withState(state: TagState): TagHistory = copy(state = state)
  override def addEvent(e: TagUpdate): TagHistory = copy(events = events :+ e)

}

object TagHistory extends FromJson[TagHistory] {
  val empty: TagHistory = TagHistory()
}
