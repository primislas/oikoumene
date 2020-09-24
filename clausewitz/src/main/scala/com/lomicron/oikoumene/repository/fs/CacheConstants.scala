package com.lomicron.oikoumene.repository.fs

object CacheConstants {
//  val localisation = "localisation.json"

  val provinces = "provinces.json"
  val tags = "tags.json"

  val areas = "areas.json"
  val regions = "regions.json"
  val superregions = "superregions.json"
  val continents = "continents.json"

  val terrain = "terrain.json"
  val climate = "climate.json"

  val tradeNodes = "trade_nodes.json"
  val tradeGoods = "trade_goods.json"
  val colonies = "colonies.json"

  val religions = "religions.json"
  val religionGroups = "religion_groups.json"
  val cultures = "cultures.json"
  val cultureGroups = "culture_groups.json"

  val map = "map.json"
  val positions = "positions.json"
  val routes = "routes.json"
  val elevatedLakes = "elevated_lakes.json"

  val requiredFiles = Seq(
//    localisation,
    provinces, tags,
    areas, regions, superregions, continents,
    terrain, climate, tradeNodes, tradeGoods, colonies,
    religions, religionGroups, cultures, cultureGroups,
    map, positions, routes, elevatedLakes
  )

}
