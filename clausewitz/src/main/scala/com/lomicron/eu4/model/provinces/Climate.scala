package com.lomicron.eu4.model.provinces

import com.fasterxml.jackson.annotation.JsonCreator
import com.lomicron.oikoumene.model.Entity
import com.lomicron.oikoumene.model.localisation.Localisation
import com.lomicron.utils.json.FromJson

import scala.collection.immutable.ListSet

@JsonCreator
case class Climate
(// hits = 10, isOptional = false, sample = "arctic"
 id: String = Entity.UNDEFINED,
 // hits = 10, isOptional = false, sample = [1034,1035,1036,1037,1038,1039,1040,1041,1042,1043,1044,1045,1046,1047,1051,1052,1054,1055,1059,1061,1062,1064,1067,1068,1069,1070,1071,1072,1073,1074,1075,1076,1077,1078,1080,1963,1964,1033,2427,2425,2426,1780,2445,2428,2429,2433,2430,2432,2443,2431,2436,2442,2437,2435,2446,2438,2439,2440,977,978,979,996,997,998,999,1004,1005,1006,1009,2591,2594,2592,2579,2577,2576,2578,2575,2574,2593,2611,2612,2613,676,705,706,1048,2102,2103,2128,2134,4204,4205,4514,4515,4516,4518,4519,4520,1781,1782,1783,1787,1804,1805,1806,1807,1810,1811,1812,1814,370,371,1104,1105,21,18,1777,1776,315,1955,313,4113,4114,4151,4152,4256,4257,4258,2025]
 provinceIds: ListSet[Int] = ListSet.empty,
 // hits = 9, isOptional = true, sample = {"name":"Arctic"}
 localisation: Localisation = Localisation.empty,
) extends Entity {
  def hasProvince(provinceId: Int): Boolean = provinceIds.contains(provinceId)
}

object Climate extends FromJson[Climate]

