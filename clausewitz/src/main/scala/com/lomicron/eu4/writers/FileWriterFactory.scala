package com.lomicron.eu4.writers
import com.lomicron.eu4.model.politics.Tag
import com.lomicron.eu4.model.provinces.Province
import com.lomicron.eu4.repository.fs.FileResourceRepository
import com.lomicron.eu4.writers.provinces.ProvinceHistoryWriter
import com.lomicron.eu4.writers.politics.TagHistoryWriter

case class FileWriterFactory(settings: ModSettings, repo: FileResourceRepository) extends WriterFactory {
  override def tagHistoryWriter: FileModWriter[Tag] = TagHistoryWriter(settings, repo)
  override def provinceHistoryWriter: FileModWriter[Province] = ProvinceHistoryWriter(settings, repo)
}

object FileWriterFactory {
  def apply(repo: FileResourceRepository): FileWriterFactory = {
    val settings = ModSettings(eu4ModDir = repo.settings.modDir)
    FileWriterFactory(settings, repo)
  }
}
