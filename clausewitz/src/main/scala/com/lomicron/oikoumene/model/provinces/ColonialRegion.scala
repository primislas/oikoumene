package com.lomicron.oikoumene.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.{Color, Entity}
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson
import com.lomicron.utils.json.JsonMapper.JsonMap

@JsonCreator
case class ColonialRegion
(// hits = 31, isOptional = false, sample = "colonial_alaska"
 id: String = Entity.UNDEFINED,
 // hits = 31, isOptional = false, sample = [225,225,225]
 color: Color = Color.black,
 // hits = 12, isOptional = true, sample = {"name":"Colonial Cascadia"}
 localisation: Localisation = Localisation.empty,
 provinceIds: Seq[Int] = Seq.empty,
 // hits = 12, isOptional = true, sample = [{"trigger":{"primary_culture":"russian"},"name":"COLONIAL_ALASKA_Alyeska"},{"trigger":{"OR":{"tag":["SPA","CAS","ARA"]}},"name":"COLONIAL_ALASKA_Pacifico_Norte"},{"name":"COLONIAL_CALIFORNIA_Cascadia"},{"name":"COLONIAL_ALASKA_Alaska"},{"name":"COLONIAL_REGION_New_Root_GetName"},{"name":"COLONIAL_ALASKA_Aleutia"}]
 names: Seq[JsonMap] = Seq.empty,
 // hits = 11, isOptional = true, sample = {"inuit":10,"aleutian":8}
 culture: Option[JsonMap] = None,
 // hits = 11, isOptional = true, sample = 1
 nativeFerocity: Int = 0,
 // hits = 11, isOptional = true, sample = 4
 nativeHostileness: Int = 0,
 // hits = 11, isOptional = true, sample = 8
 nativeSize: Int = 0,
 // hits = 11, isOptional = true, sample = {"shamanism":10}
 religion: Option[JsonMap] = None,
 // hits = 11, isOptional = true, sample = 0
 taxIncome: Option[Int] = None,
 // hits = 11, isOptional = true, sample = {"fur":10,"fish":3,"naval_supplies":2,"gold":2}
 tradeGoods: Option[JsonMap] = None,
) extends Entity

object ColonialRegion extends FromJson[ColonialRegion]
