package com.lomicron.oikoumene.model.history

import com.lomicron.utils.parsing.tokenizer.Date

trait HistEvent {
  val date: Option[Date] = None
}
