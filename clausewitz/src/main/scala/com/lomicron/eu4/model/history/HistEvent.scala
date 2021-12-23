package com.lomicron.eu4.model.history

import com.lomicron.utils.parsing.tokenizer.Date

trait HistEvent {
  val date: Option[Date] = None
}
