package com.lomicron.oikoumene.writers.politics

import com.lomicron.oikoumene.io.FileNameAndContent
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.serializers.ClausewitzSerializer
import com.lomicron.oikoumene.writers.{FileModWriter, ModSettings}

case class TagHistoryWriter(settings: ModSettings, repo: FileResourceRepository) extends FileModWriter[Tag] {

  def serialize(tag: Tag): FileNameAndContent = {
    val filename = tag.history.sourceFile.getOrElse(defaultFileName(tag))
    val content = ClausewitzSerializer.serializeHistory(tag.history)

    FileNameAndContent(filename, content)
  }

  def defaultFileName(tag: Tag): String =
    s"${tag.id} - ${tag.localisation.name.getOrElse("")}.txt"

  override def relativeTargetDir: String = repo.countryHistoryDir

}
