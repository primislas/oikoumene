package com.lomicron.oikoumene.writers.provinces

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.ResourceNameAndContent
import com.lomicron.oikoumene.serializers.ClausewitzSerializer

object ProvinceWriter {

  def serialize(province: Province): ResourceNameAndContent = {
    val filename = province.history.sourceFile.getOrElse(defaultFileName(province))
    val content = ClausewitzSerializer.serializeHistory(province.history)

    ResourceNameAndContent(filename, content)
  }

  def defaultFileName(province: Province): String =
    s"${province.id} - ${province.localisation.name.getOrElse("")}.txt"

}
