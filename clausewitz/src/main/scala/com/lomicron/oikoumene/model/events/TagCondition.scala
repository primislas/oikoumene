package com.lomicron.oikoumene.model.events

import com.fasterxml.jackson.annotation.JsonCreator

class TagCondition
(
  isExcommunicated: Option[Boolean] = None,
  religion: Option[String] = None,
) {
  @JsonCreator def this() = this(None)
}
