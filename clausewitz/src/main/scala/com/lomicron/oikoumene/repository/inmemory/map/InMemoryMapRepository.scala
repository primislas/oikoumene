package com.lomicron.oikoumene.repository.inmemory.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.model.map.Tile
import com.lomicron.oikoumene.repository.api.map.MapRepository
import com.lomicron.oikoumene.repository.inmemory.InMemoryCrudRepository

import scala.collection.immutable.SortedMap

case class InMemoryMapRepository()
  extends InMemoryCrudRepository[Color, Tile](t => Option(t.color))
    with MapRepository
{
  override def setId(entity: Tile, id: Color): Tile = entity.copy(color = id)

  override def findNames(keys: Seq[Color]): SortedMap[Color, String] = SortedMap.empty

}
