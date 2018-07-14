package com.lomicron.oikoumene.engine

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.toJsonNode
import com.lomicron.utils.parsing.Date
import org.specs2.mutable.Specification

class ClausewitzParserSpec extends Specification {

  val provinceFile = "151 - Constantinople.txt"
  val year = 1245
  val month = 2
  val day = 21

  "ClausewitzParser#strToDate" should {
    "- construct a date from a valid string" >> {
      val str = s"$year.$month.$day"
      val dateOpt = ClausewitzParser.strToDate(str)
      dateOpt mustEqual Option(Date(year, month, day))
      dateOpt.map(_.toString) mustEqual Option(str)
    }

    "- return an empty Option for an invalid string" >> {
      val str = "not a date 1253.12.43"
      val dateOpt = ClausewitzParser.strToDate(str)
      dateOpt mustEqual Option.empty
    }
  }

  "ClausewitzParser#date2json" should "- construct a JSON object from a date" >> {
    val date = Date(year, month, day)
    val obj = ClausewitzParser.date2json(date)
    obj.get(ClausewitzParser.yearField).asInt() mustEqual year
    obj.get(ClausewitzParser.monthField).asInt() mustEqual month
    obj.get(ClausewitzParser.dayField).asInt() mustEqual day
  }

  "ClausewitzParser#fieldWithoutPrefix" should "- remove prefix and convert a field to plural" >> {
    val str = "add_core"
    val prefix = "add_"
    val unprefixed = ClausewitzParser.fieldWithoutPrefix(str, prefix)
    unprefixed mustEqual "cores"
  }

  "CalusewitzParser#mergeField" should {

    def validObject = JsonMapper
      .objectNode
      .set("cores", JsonMapper.arrayNodeOf("BYZ")).asInstanceOf[ObjectNode]
      .set("owner", toJsonNode("BYZ")).asInstanceOf[ObjectNode]
      .set("claims", JsonMapper.arrayNode).asInstanceOf[ObjectNode]

    "- update an ordinary key" >> {
      val obj = validObject
      val field = "owner"
      val newOwner = toJsonNode("VEN")
      ClausewitzParser.mergeField(obj, field, newOwner)
      obj.get(field) mustEqual newOwner
    }

    "- add an item to a key array for a field that starts with add_" >> {
      val addCore = "add_core"
      val newCore = toJsonNode("VEN")
      val cores = "cores"

      val obj = ClausewitzParser.mergeField(validObject, addCore, newCore)
      obj.get(cores) mustEqual JsonMapper.arrayNodeOf("BYZ", "VEN")
    }

    "- remove a single item from an array for a field that starts with remove_" >> {
      val removeCore = "remove_core"
      val removedCore = toJsonNode("BYZ")
      val cores = "cores"

      val obj = ClausewitzParser.mergeField(validObject, removeCore, removedCore)
      obj.get(cores) mustEqual JsonMapper.arrayNode
    }

    "- do nothing on an empty key when receiving a remove_ command" >> {
      val removeClaim = "remove_claim"
      val removedClaim = toJsonNode("BYZ")
      val claims = "claims"

      val obj = ClausewitzParser.mergeField(validObject, removeClaim, removedClaim)
      obj.get(claims) mustEqual JsonMapper.arrayNode
    }

  }

}
