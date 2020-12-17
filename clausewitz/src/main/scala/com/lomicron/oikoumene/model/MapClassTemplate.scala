package com.lomicron.oikoumene.model

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, numericNode}
import com.typesafe.scalalogging.LazyLogging

object MapClassTemplate extends LazyLogging {

  def add(a: ObjectNode, b: ObjectNode): ObjectNode =
    combine(a, b, _ + _)

  def remove(a: ObjectNode, b: ObjectNode): ObjectNode =
    combine(a, b, _ - _)

  def combine(a: ObjectNode, b: ObjectNode, f: (Double, Double) => Double): ObjectNode = {
    val merged = a.deepCopy()
    b.entrySeq().foreach(e => {
      val (k, v) = (e.getKey, e.getValue)
      a
        .getNumber(k)
        .map(n => {
          if (v.isNumber) {
            val res = f(n.doubleValue(), v.doubleValue())
            val resNode = numericNode(res)
            merged.setEx(k, resNode)
          } else {
            logger.warn(s"Trying to add incompatible types on field $k: '$v', '$n'")
            n
          }
        })
        .getOrElse(merged.setEx(k, v))
    })

    merged
  }

}
