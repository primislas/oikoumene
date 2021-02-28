package com.lomicron.oikoumene.model.map

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.utils.geometry.Point2D
import com.lomicron.utils.json.FromJson

@JsonCreator
case class ElevatedLake(triangleStrip: Seq[Int] = Seq.empty, height: Int = 0)
  extends FromJson[ElevatedLake]
{

  def asPolygon(mapHeight: Option[Int] = None): Seq[Point2D] = {
    val (head, tail) = triangleStrip
      .grouped(2)
      .map(coords => Point2D(coords.head, coords.last))
      .map(p => mapHeight.map(h => p.copy(y = h - p.y)).getOrElse(p))
      .zipWithIndex.toList
      .partition(_._2 % 2 == 1)
    head.map(_._1) ++ tail.map(_._1).reverse
  }

}

object ElevatedLake extends FromJson[ElevatedLake]
