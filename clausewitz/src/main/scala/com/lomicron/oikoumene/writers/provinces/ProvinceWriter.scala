package com.lomicron.oikoumene.writers.provinces

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.FileNameAndContent
import com.lomicron.oikoumene.serializers.ClausewitzSerializer

object ProvinceWriter {

  def serialize(province: Province): FileNameAndContent = {
    val filename = province.history.sourceFile.getOrElse(defaultFileName(province))
    val content = ClausewitzSerializer.serializeHistory(province.history)

    FileNameAndContent(filename, content)
  }

  def defaultFileName(province: Province): String =
    s"${province.id} - ${province.localisation.name.getOrElse("")}.txt"

}
