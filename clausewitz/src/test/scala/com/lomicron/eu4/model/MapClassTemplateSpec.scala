package com.lomicron.eu4.model

import com.fasterxml.jackson.databind.JsonNode
import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class MapClassTemplateSpec extends Specification {

  "MapClassTemplateSpec" should {

    "- add numeric fields" in {
      val nField = "numField"
      val n1 = 2
      val n2 = -0.15
      val expected: JsonNode = JsonMapper.numericNode(n1 + n2)

      val jm1 = JsonMapper.toObjectNode(Map(nField -> n1))
      val jm2 = JsonMapper.toObjectNode(Map(nField -> n2))
      val sum = for {
        m1 <- jm1
        m2 <- jm2
      } yield MapClassTemplate.add(m1, m2).get(nField)

      sum must beSome(expected)
    }

    "- override non-numeric fields" in {
      val (f1, b1, b2) = ("f1", false, true)
      val (f2, s1, s2) = ("f2", "stale", "updated")
      val o1 = JsonMapper.toObjectNode(Map(f1 -> b1, f2 -> s1))
      val o2 = JsonMapper.toObjectNode(Map(f1 -> b2, f2 -> s2))
      val merged = for {
        m1 <- o1
        m2 <- o2
      } yield MapClassTemplate.add(m1, m2)

      val mf1 = merged.map(_.get(f1)).flatMap(Option(_))
      val mf2 = merged.map(_.get(f2)).flatMap(Option(_))

      mf1 must beEqualTo(o2.map(_.get(f1)))
      mf2 must beEqualTo(o2.map(_.get(f2)))
    }

  }
}
