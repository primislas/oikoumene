package com.lomicron.oikoumene.model.politics

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.history.History
import com.lomicron.utils.json.FromJson

case class TagHistory
(
  override val init: TagUpdate = TagUpdate.empty,
  override val state: TagState = TagState.empty,
  override val events: Seq[TagUpdate] = Seq.empty,
  override val sourceFile: Option[String] = None,
) extends History[TagHistory, TagState, TagUpdate] {

  @JsonCreator def this() = this(TagUpdate.empty)

  override def withState(state: TagState): TagHistory = copy(state = state)

}

object TagHistory extends FromJson[TagHistory] {
  val empty: TagHistory = TagHistory()
}