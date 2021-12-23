package com.lomicron.eu4.writers.politics

import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.serializers.ClausewitzSerializer

case class TagWriter() {

  def serializeHistory(tag: Tag): String =
    ClausewitzSerializer.serializeHistory(tag.history)

}
