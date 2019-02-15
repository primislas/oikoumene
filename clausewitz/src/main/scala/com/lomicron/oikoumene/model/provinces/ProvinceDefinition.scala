package com.lomicron.oikoumene.model.provinces

import com.lomicron.oikoumene.model.map.Color

case class ProvinceDefinition
(id: Int,
 color: Color,
 comment: String,
 tag2: Option[String])

object ProvinceDefinition {
  def apply(id: Int, color: Color, comment: String):
  ProvinceDefinition = ProvinceDefinition(id, color, comment, None)

  def apply(id: Int, color: Color, comment: String, tag2: String):
  ProvinceDefinition = ProvinceDefinition(id, color, comment, Some(tag2))
}