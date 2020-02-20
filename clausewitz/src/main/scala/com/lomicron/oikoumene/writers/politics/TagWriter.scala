package com.lomicron.oikoumene.writers.politics

import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.serializers.ClausewitzSerializer

case class TagWriter() {

  def serializeHistory(tag: Tag): String =
    ClausewitzSerializer.serializeHistory(tag.history)

}
