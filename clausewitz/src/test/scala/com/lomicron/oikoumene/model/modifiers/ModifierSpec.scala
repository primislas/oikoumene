package com.lomicron.oikoumene.model.modifiers

import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx
import com.lomicron.utils.collection.CollectionUtils.toOption

class ModifierSpec extends Specification {

  "Modifier" should {

    val json = """{"id":"dummy_modifier","land_maintenance_modifier":-0.15,"advisor_pool":1,"can_fabricate_for_vassals":true}"""

    "- be correctly deserialized from json" >> {
      val modifier = Modifier.fromJson(json)
      modifier.id must beSome("dummy_modifier")
      modifier.landMaintenanceModifier must beSome(-0.15)
      modifier.advisorPool must beSome(1)
      modifier.canFabricateForVassals must beSome(true)
    }


    "- be correctly serialized to json" >> {
      val canFabricateForVassals = true
      val conf = JsonMapper.objectNode
        .setEx("land_maintenance_modifier", -0.15)
        .setEx("advisor_pool", 1)
        .setEx("can_fabricate_for_vassals", canFabricateForVassals)
      val modifier = Modifier(id = "dummy_modifier", conf = conf)
      val j = JsonMapper.toJson(modifier)
      j mustEqual json
    }

  }

}
