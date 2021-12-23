package com.lomicron.eu4.parsers

import com.lomicron.eu4.parsers.ConfigField.ValueTypes
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
