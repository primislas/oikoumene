package com.lomicron.oikoumene.writers.map

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.io.FileNameAndContent
import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.fs.FileResourceRepository
import com.lomicron.oikoumene.serializers.ClausewitzSerializer
import com.lomicron.oikoumene.writers.{FileModWriter, ModSettings}
import com.lomicron.utils.json.JsonMapper._

case class ProvinceHistoryWriter(settings: ModSettings, repo: FileResourceRepository) extends FileModWriter[Province] {

  def serialize(province: Province): FileNameAndContent = {
    val filename = province.history.sourceFile.getOrElse(defaultFileName(province))
    val content = ClausewitzSerializer.serializeHistory(province.history, adapt)

    FileNameAndContent(filename, adapt(content))
  }

  def adapt(ph: ObjectNode): ObjectNode = {
    ph.entries().foreach { case (k, v) =>
      k match {
        case "add_building" =>
          if (v.isArray)
            v.asArray.map(_.toSeq).getOrElse(Seq.empty)
              .foreach(b => ph.setEx(b.asText(), "yes"))
          else
            ph.setEx(v.asText(), "yes")
          ph.removeEx("add_building")
        case _ =>
      }
    }

    ph
  }

  def adapt(c: String): String =
    c.replace("latent_trade_goods = coal", "latent_trade_goods = { coal }")

  def defaultFileName(province: Province): String =
    s"${province.id} - ${province.localisation.name.getOrElse("")}.txt"

  override def relativeTargetDir: String = repo.provinceHistoryDir
}

