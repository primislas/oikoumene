package com.lomicron.oikoumene.repository.inmemory

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.utils.json.JsonMapper.ObjectNodeEx

import scala.collection.immutable.SortedMap

class InMemoryObjectNodeRepository
extends InMemoryCrudRepository[String, ObjectNode](o => Option(o.get("id")).map(_.asText())) {

  override def setId(entity: ObjectNode, id: String): ObjectNode =
    entity.setEx(Fields.idKey, id)

  override def findNames(keys: Seq[String]): SortedMap[String, String] =
    SortedMap[String, String]() ++ keys.map(k => (k, k))
}
