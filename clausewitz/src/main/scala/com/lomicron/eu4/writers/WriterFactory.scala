package com.lomicron.eu4.writers

import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.model.provinces.Province

trait WriterFactory {
  def settings: ModSettings
  def tagHistoryWriter: FileModWriter[Tag]
  def provinceHistoryWriter: FileModWriter[Province]
}
