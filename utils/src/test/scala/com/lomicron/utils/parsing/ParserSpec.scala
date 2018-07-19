package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.ArrayNode
import org.specs2.mutable.Specification
import com.lomicron.utils.io.IO
import com.lomicron.utils.json.JsonMapper

class ParserSpec extends Specification {
  val provinceFile = "151 - Constantinople.txt"

  "JsonParser#parse" should {
    "" >> {
      val content = IO.readTextResource(provinceFile)
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      val json = node.toString
      json.length must be_>(0)
    }

    "- recognize an array" >> {
      val field = "color"
      val content = s"$field = {123 125 123}"
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      val array = node.get(field)
      array must beAnInstanceOf[ArrayNode]
      array.asInstanceOf[ArrayNode].size mustEqual 3
    }

    "- recognize an array of string and Win-1252 characters" >> {
      val content = s"""leader_names = {
                      Blittersdorf
                      "von Gelnhausen"
                      "von Schöneck"
                      "von Dieblich"
                      Cölln
                      Bensenraede
                      Adenau
                      Gruithausen
                      Hohenfeld
                      "von Namedy"
                      Falkenberg
                      "von Clausbruch"
                      Hillensberg
                      Dammerscheidt
                      Elmpt
                      Naxleden
                      Schaluyn
                      "von Leyen"
                      Ansembourg
                      Zebnitsch
                    }"""
      val tokens = Tokenizer.tokenize(content)

      val invalidIdentifiers = tokens.filter(_.isInstanceOf[InvalidIdentifier])

      val node = JsonParser.parse(tokens)._1
      println(JsonMapper.toJson(node))
      node.get("leaderNames") must beAnInstanceOf[ArrayNode]
    }

    "- recognize a single element array" >> {
      val content = """army_names = {
                      "Armee von $PROVINCE$"
                    }"""
      val tokens = Tokenizer.tokenize(content)

      val invalidIdentifiers = tokens.filter(_.isInstanceOf[InvalidIdentifier])

      val node = JsonParser.parse(tokens)._1
      println(JsonMapper.toJson(node))
      node.get("leaderNames") must beAnInstanceOf[ArrayNode]
    }
  }

}