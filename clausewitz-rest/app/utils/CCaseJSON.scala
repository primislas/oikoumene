package utils

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.lomicron.utils.json.JsonMapper

object CCaseJSON {

  private val om = JsonMapper.defaultObjectMapper()
  om.setPropertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)
  private val mapper = JsonMapper(om)

  def toJson(obj: AnyRef): String = mapper.toJson(obj)

  def prettyPrint(obj: AnyRef): String = mapper.prettyPrint(obj)

  def fromJson[T: Manifest](json: String): T = mapper.fromJson[T](json)

}
