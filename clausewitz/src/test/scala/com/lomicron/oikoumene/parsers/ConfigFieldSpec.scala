package com.lomicron.oikoumene.parsers

import com.lomicron.oikoumene.parsers.ConfigField.ValueTypes
import com.lomicron.utils.json.JsonMapper
import org.specs2.mutable.Specification

class ConfigFieldSpec extends Specification {

  "ConfigField#getNodeType" should {
    "- identify negative integer as Int" >> {
      val n = JsonMapper.numericNode(-1)
      val nType = ConfigField.getNodeType(n)
      nType mustEqual ValueTypes.integer
    }
  }

}
