package com.lomicron.oikoumene.model.trade

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.events.ProvinceCondition
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.oikoumene.model.modifiers.Modifier
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.utils.json.FromJson

@JsonCreator
case class TradeGood
(
  // hits = 31, isOptional = false, sample = "grain"
  id: String = Entity.UNDEFINED,
  // hits = 31, isOptional = false, sample = {"name":"Grain"}
  localisation: Localisation = Localisation.empty,
  // hits = 31, isOptional = false, sample = "00_tradegoods.txt"
  sourceFile: String = Entity.UNDEFINED,
  // hits = 31, isOptional = false, sample = [0.96,0.93,0.58]
  index: Option[Int] = None,
  color: Color = Color.black,
  price: BigDecimal = BigDecimal(0),
  // hits = 30, isOptional = true, sample = {"factor":35,"modifier":[{"factor":0,"area":"newfoundland_area"},{"factor":0,"OR":{"has_terrain":["desert","glacier","coastline"],"has_climate":"arctic"}},{"factor":0.15,"OR":{"has_terrain":["mountain","coastal_desert","forest","woods","hills","jungle"]}},{"factor":0.25,"OR":{"has_terrain":["highlands","marsh"]}},{"factor":0.25,"OR":{"has_climate":["arid","tropical"]}},{"factor":0.6,"OR":{"has_terrain":["drylands","savannah","steppe"]}},{"factor":0.35,"has_winter":"severe_winter"},{"factor":0.5,"has_winter":"normal_winter"},{"factor":1.5,"has_terrain":"farmlands"},{"factor":1.5,"OR":{"region":["poland_region","ruthenia_region"]}}]}
  chance: Option[ProvinceCondition] = None,
  // hits = 29, isOptional = true, sample = {"land_forcelimit_modifier":0.20}
  modifier: Option[Modifier] = None,
  // hits = 29, isOptional = true, sample = {"land_forcelimit":0.5}
  province: Option[Modifier] = None,

  // --- Latent goods
  // hits = 1, isOptional = true, sample = true
  isLatent: Boolean = false,
  // hits = 1, isOptional = true, sample = true
  isValuable: Boolean = false,
  // hits = 1, isOptional = true, sample = 7
  rnwLatentChance: Option[Int] = None,
  // hits = 1, isOptional = true, sample = {"OR":{"development":20,"owner":{"innovativeness":20}},"provincial_institution_progress":{"which":"enlightenment","value":100},"owner":{"has_institution":"enlightenment"}}
  trigger: Option[ProvinceCondition] = None,

) extends Entity {
  def withPrice(p: BigDecimal): TradeGood = copy(price = p)
}

object TradeGood extends FromJson[TradeGood]
