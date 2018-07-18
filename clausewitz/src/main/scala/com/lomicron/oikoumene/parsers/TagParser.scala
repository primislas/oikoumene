package com.lomicron.oikoumene.parsers

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.parse
import com.lomicron.utils.collection.CollectionUtils._
import com.typesafe.scalalogging.LazyLogging

object TagParser extends LazyLogging {

  def apply(
             tags: Map[String, String],
             countries: Map[String, String],
             histories: Map[String, String],
             names: Map[String, String]): Seq[ObjectNode] = {

    def tagToCoutry(tag: String) =
      countries
        .get(tag)
        .map(parse)
//        .map(_._1)
//        .map(o => o.put("tag", tag))



    val withCountryConfig = tags
        .mapKeyToValue(tagToCoutry)
        .filterKeyValue((tag, opt) => {
          if (opt.isEmpty)
            logger.warn(s"Tag $tag has no country configuration")
          opt.isEmpty
        })
        .mapValuesEx(_.get)
        .mapKVtoValue((tag, t2) => {
          val errors = t2._2
          if (errors.nonEmpty)
            logger.warn(s"Encountered errors parsing country configuration for tag '$tag': $errors")
          t2._1
        })



    Seq.empty
  }
}
