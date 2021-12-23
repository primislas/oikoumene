package com.lomicron.eu4.repository.api.trade

import com.lomicron.eu4.model.modifiers.Modifier
import com.lomicron.eu4.model.provinces.{Province, ProvinceState}
import com.lomicron.eu4.model.trade.CenterOfTrade
import com.lomicron.oikoumene.repository.api.AbstractRepository
import com.lomicron.utils.collection.CollectionUtils.toOption

trait CenterOfTradeRepository extends AbstractRepository[String, CenterOfTrade] {

  def ofProvince(p: Province): Option[CenterOfTrade] = {
    val cotType = if (p.geography.landlocked) CenterOfTrade.Types.inland else CenterOfTrade.Types.coastal
    find(p.state.centerOfTrade, cotType)
  }

  def ofProvince(ps: ProvinceState, isLandlocked: Option[Boolean] = None): Option[CenterOfTrade] =
    if (ps.centerOfTrade <= 0) None
    else {
      val cotType = isLandlocked
        .map(landlocked => if (landlocked) CenterOfTrade.Types.inland else CenterOfTrade.Types.coastal)
        .getOrElse(
          if (!ps.activeModifiers.contains("coastal"))
            CenterOfTrade.Types.inland
          else
            CenterOfTrade.Types.coastal
        )
      find(ps.centerOfTrade, cotType)
    }


  def find(level: Int, cotType: String): Option[CenterOfTrade] =
    if (level <= 0) None
    else findAll.filter(_.level == level).find(_.`type` == cotType)

  def globalModifiers(ps: Seq[Province]): Seq[Modifier] =
    ps.flatMap(p => {
      val cot = ofProvince(p)
      cot.flatMap(_.globalModifiers).map(_.copy(id = globalModifierId(p.id, cot.get.id)))
    })

  def globalModifierId(provId: Int, cotId: String) =
    s"PROV${provId}_$cotId"


}
