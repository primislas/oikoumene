package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.io.IO
import com.lomicron.utils.parsing.tokenizer.Tokenizer
import org.specs2.mutable.Specification

import scala.jdk.CollectionConverters.IteratorHasAsScala

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

    "- recognize an array of strings with Win-1252 characters" >> {
      val content = """leader_names = {
                      Blittersdorf
                      "von Gelnhausen"
                      "von Schöneck"
                      "von Dieblich"
                      öCölln
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
      val node = JsonParser.parse(tokens)._1
      node.get("leader_names") must beAnInstanceOf[ArrayNode]
    }

    "- recognize a single element array" >> {
      val content = """army_names = {
                      "Armee von $PROVINCE$"
                    }"""
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      val armyNames = node.get("army_names")
      armyNames must beAnInstanceOf[ArrayNode]
      armyNames.asInstanceOf[ArrayNode].size mustEqual 1
    }

    "- handle comments in arrays" >> {
      val content = """leader_names = {
                          # Daimyo names
                      	Akamatsu Akechi Ashikaga Amago Kyogoku Rokkaku Asakura Asano Aso Chosokabe Date Hachisuka
                      	Hatakeyama Hojo Hosokawa Ichijo Keda Imagawa Isshiki Ito Jinbo Kikuchi Kono Maeda
                      	# Akamatsu Flavor
                      	Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu Akamatsu
                          # Retainers of the Akamatsu clan
                      	Hirano Hirano Hirano Hirano Hirano
                      	# Region Flavor: Chugoku
                      	Ouchi Sue Sugi Naito
                      }"""
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      node.get("leader_names") must beAnInstanceOf[ArrayNode]
    }

    "- recognize single quotation marks as identifiers " >> {
      val content = """ship_names = {
            "N. Sra. Madre de Deus e São José"
            "Nossa Senhora da Glória"
            "Nossa Senhora das Necessidades"
            "Nossa Senhora da Conceição"'
            "Nossa Senhora da Esperanca"
            Pérola
            "Princesa Carlota"
      }"""
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      node.get("ship_names") must beAnInstanceOf[ArrayNode]
    }

    "- make the best guess about selecting " +
      "an array or an object based on field count and array size" >> {
      val content = """monarch_names = {
                        "Filipe #0" = 60
                        "Afonso #4 = 40"
                        "Pedro #1" = 40
                        "António #0" = 20
                    	}"""
      val tokens = Tokenizer.tokenize(content)
      val (node, errors) = JsonParser.parse(tokens)
      node.get("monarch_names") must beAnInstanceOf[ObjectNode]
      val nameCount = Option(node.get("monarch_names"))
          .map(_.asInstanceOf[ObjectNode])
          .map(_.fieldNames().asScala.toSeq.size)
          .getOrElse(0)
      nameCount must_== 3
      errors.size must_== 1
    }

    "- properly return scope after single element arrays" >> {
      // scope sequence in this case ends up as
      // ROOT -> Field Scope (army_names) -> Field Scope (fleet_names)
      val content = """
                      army_names = {
                      	 "Army of $PROVINCE$"
                      }
                      fleet_names = {
                      	 "Fleet of $PROVINCE$"
                      }
                    """
      val (node, errors) = JsonParser.parse(content)
      node.asInstanceOf[ObjectNode].fieldNames().asScala.toSeq.size mustEqual 2
      errors.size mustEqual 0
    }

    "- properly process arrays of dates" >> {
      val content = """monsoon = {
                      		00.06.01
                      		00.09.30
                    }"""
      val (node, errors) = JsonParser.parse(content)
      val dates = node.get("monsoon")
      dates.get(0).asText() mustEqual "0.6.1"
      dates.get(1).asText() mustEqual "00.09.30"
      errors.size mustEqual 0
    }

    "- merge two values under the same key into an array" >> {
      // TODO don't forget about these nonsense monsoon declarations when serializing
      //  These are actually arrays of arrays!
      val content = """
                      monsoon = {
                        00.11.01
                        00.12.30
                      }
                      monsoon = {
                        00.01.01
                        00.04.30
                      }
                    """
      val (node, errors) = JsonParser.parse(content)
      errors.size mustEqual 0

      val dates = node.get("monsoon")
      dates.size() mustEqual 4

      // TODO: should be 2 arrays of arrays
//      val dates = node.get("monsoon")
//      dates.size() mustEqual 2
//
//      import com.lomicron.utils.json.JsonMapper.{ArrayNodeEx, JsonNodeEx}
//      val isArrayOfArrays = dates.asArray.map(_.toSeq).getOrElse(Seq.empty).map(_.isArray).forall(identity)
//      isArrayOfArrays must beTrue
    }

  }

}
