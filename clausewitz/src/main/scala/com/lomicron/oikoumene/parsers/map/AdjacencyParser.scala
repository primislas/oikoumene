package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.map.Adjacency

import scala.util.matching.Regex

trait AdjacencyParser {

  val adjacencyPat: Regex =
    "^(?<from>\\d+);(?<to>\\d+);(?<type>[a-zA-Z]*);(?<throuh>\\d+);-?(?<startX>\\d+);-?(?<startY>\\d+);-?(?<stopX>\\d+);-?(?<stopY>\\d+);(?<comment>.*)".r

  def parseAdjacencies(data: String): Seq[Adjacency] =
    data
      .linesIterator
      .toList
      .flatMap(parseAdjacency)

  def parseAdjacency(line: String): Option[Adjacency] =
    line match {
      case adjacencyPat(from, to, aType, through, startX, startY, stopX, stopY, comment) =>
        Some(Adjacency(from.toInt, to.toInt, aType, through.toInt, startX.toInt, startY.toInt, stopX.toInt, stopY.toInt, comment))
      case _ => None
    }

}
