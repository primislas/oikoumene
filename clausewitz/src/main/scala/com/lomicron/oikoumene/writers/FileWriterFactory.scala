package com.lomicron.oikoumene.writers
import com.lomicron.oikoumene.model.politics.Tag
import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.writers.map.ProvinceHistoryWriter
import com.lomicron.oikoumene.writers.politics.TagHistoryWriter

case class FileWriterFactory(settings: ModSettings, repo: FileResourceRepository) extends WriterFactory {
  override def tagHistoryWriter: FileModWriter[Tag] = TagHistoryWriter(settings, repo)
  override def provinceHistoryWriter: FileModWriter[Province] = ProvinceHistoryWriter(settings, repo)
}

object FileWriterFactory {
  def apply(repo: FileResourceRepository): FileWriterFactory = {
    val settings = ModSettings(eu4ModDir = Some(repo.modDir))
    FileWriterFactory(settings, repo)
  }
}
