package com.lomicron.eu4.writers.politics

import com.lomicron.eu4.io.FileNameAndContent
import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.repository.fs.FileResourceRepository
import com.lomicron.eu4.serializers.ClausewitzSerializer
import com.lomicron.eu4.writers.{FileModWriter, ModSettings}

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
