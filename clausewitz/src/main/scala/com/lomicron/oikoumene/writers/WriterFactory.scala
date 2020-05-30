package com.lomicron.oikoumene.writers

import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.model.provinces.Province

trait WriterFactory {
  def settings: ModSettings
  def tagHistoryWriter: FileModWriter[Tag]
  def provinceHistoryWriter: FileModWriter[Province]
}
