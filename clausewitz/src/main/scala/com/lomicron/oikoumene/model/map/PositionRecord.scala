package com.lomicron.oikoumene.model.map

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.parsers.map.Point2D
import com.lomicron.utils.json.FromJson

case class PositionRecord
(
  id: Int,
  position: Vector[Double] = Vector.empty,
  rotation: Vector[Double] = Vector.empty,
  height: Vector[Double] = Vector.empty,
) extends FromJson[PositionRecord] {

  @JsonCreator def this() = this(-1)

  def toConf: ProvincePositions = {
    val city = positionAt(0)
    val unit = positionAt(1)
    val text = positionAt(2)
    val port = positionAt(3)
    val tradeRoute = positionAt(4)
    val fightingUnit = positionAt(5)
    val tradeWind = positionAt(6)

    ProvincePositions(id, city, unit, text, port, tradeRoute, fightingUnit, tradeWind)
  }

  private def positionAt(id: Int): PositionConf = {
    val px = position(2 * id)
    val py = position(2 * id + 1)
    PositionConf(Point2D(px, py), rotation(id), height(id))
  }

}

object PositionRecord extends FromJson[PositionRecord]
