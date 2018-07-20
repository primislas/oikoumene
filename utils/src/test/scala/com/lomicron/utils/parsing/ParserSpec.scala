package com.lomicron.utils.parsing

import com.fasterxml.jackson.databind.node.{ArrayNode, ObjectNode}
import com.lomicron.utils.io.IO
import com.lomicron.utils.parsing.tokenizer.Tokenizer
import org.specs2.mutable.Specification

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
      node.get("leaderNames") must beAnInstanceOf[ArrayNode]
    }

    "- recognize a single element array" >> {
      val content = """army_names = {
                      "Armee von $PROVINCE$"
                    }"""
      val tokens = Tokenizer.tokenize(content)
      val node = JsonParser.parse(tokens)._1
      val armyNames = node.get("armyNames")
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
      node.get("leaderNames") must beAnInstanceOf[ArrayNode]
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
      node.get("shipNames") must beAnInstanceOf[ArrayNode]
    }

    "- make the best decision about selecting " +
      "an array or an object based on parsing error counts" >> {
      val content = """monarch_names = {
                        "Filipe #0" = 60
                        "Afonso #4 = 40"
                        "Pedro #1" = 40
                        "António #0" = 20
                    	}"""
      val tokens = Tokenizer.tokenize(content)
      val (node, errors) = JsonParser.parse(tokens)
      node.get("monarchNames") must beAnInstanceOf[ObjectNode]
      errors.size must_== 3
    }

  }

}