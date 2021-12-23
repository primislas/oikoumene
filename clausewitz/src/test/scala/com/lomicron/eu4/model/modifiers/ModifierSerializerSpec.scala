package com.lomicron.eu4.model.modifiers

import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import org.specs2.mutable.Specification

class ModifierSerializerSpec extends Specification {

  "ModifierSerializer" should {

    "- serialize a modifier to json" >> {
      val expected = """{"id":"modifier","localisation":{"name":"City"},"source_file":"00_static_modifiers.txt","local_tax_modifier":0.25,"allowed_num_of_buildings":2,"local_sailors_modifier":0.25,"garrison_growth":0.05}""".stripMargin
      val m = Modifier(
        Some("modifier"),
        Some(Localisation(Some("City"))),
        Some("00_static_modifiers.txt"),
        JsonMapper
          .objectNode
          .setEx("local_tax_modifier", JsonMapper.numericNode(0.25))
          .setEx("allowed_num_of_buildings", JsonMapper.numericNode(2))
          .setEx("local_sailors_modifier", JsonMapper.numericNode(0.25))
          .setEx("garrison_growth", JsonMapper.numericNode(0.05))
      )
      val json = JsonMapper.toJson(m)
      json mustEqual expected
    }

  }

}
