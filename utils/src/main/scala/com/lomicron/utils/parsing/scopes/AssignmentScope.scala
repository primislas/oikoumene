package com.lomicron.utils.parsing.scopes

import com.fasterxml.jackson.databind.node.{TextNode, _}
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.parsing._
import com.lomicron.utils.parsing.tokenizer._

case class AssignmentScope(parent: Option[ObjectScope],
                           key: String)
  extends ParsingScope {

  val booleans: Set[String] = Set("yes", "no")

  def isBoolean(s: String): Boolean = booleans.contains(s)

  override def validTokens: Seq[String] = Seq("{", "identifier", "number", "date")

  override def nextScope(t: Token): (ParsingScope, ObjectNode) = {
    val value = t match {
      case OpenBrace => Option(JsonParser.objectNode)
      case Bool(_, b) => Option(BooleanNode.valueOf(b))
      case Identifier(s) => Option(TextNode.valueOf(s))
      case n: Number => Option(n.toJsonNode)
      case Date(d, _, _, _) => Option(TextNode.valueOf(d))
      case _ => None
    }

    value match {
      case Some(v) => v.textValue() match {
        case "rgb" =>
//          parent.get
//            .addField(s"${key}__rgb", JsonMapper.objectNode)
//            .nextScope(t)._1
//            .nextScope(Equals)
          (AssignmentScope(parent, s"${key}__RGB"), parsedObject)
//          parent.get.addField("rgb", JsonParser.objectNode).nextScope(Equals)
        case "hsv" => (AssignmentScope(parent, s"${key}__HSV"), parsedObject)
        case _ => (parent.get.addField(key, v), parsedObject)
      }
      case _ => addParsingError(t)
    }
  }
}
