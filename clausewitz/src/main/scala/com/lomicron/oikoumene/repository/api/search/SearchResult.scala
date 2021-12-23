package com.lomicron.oikoumene.repository.api.search

import com.lomicron.eu4.repository.api.search.SearchDictionary

case class SearchResult[T]
(
  page: Int = 1,
  size: Int = 0,
  totalPages: Int = 1,
  totalEntities: Int = 0,
  entities: Seq[T] = Seq.empty,
  dictionary: SearchDictionary = SearchDictionary.empty
)

object SearchResult {

  def apply[T](page: Int, size: Int, totalEntities: Int, entities: Seq[T]): SearchResult[T] = {
    val quotient = totalEntities / size
    val remainder = totalEntities % size
    val totalPages = if (remainder == 0 && quotient > 0) quotient else quotient + 1
    SearchResult(page, size, totalPages, totalEntities, entities)
  }

  def apply[T](req: SearchConf, entities: Seq[T]): SearchResult[T] = {
    val quotient = entities.size / req.size
    val rem = entities.size % req.size
    val matchingPages = if (quotient <= 0) 1 else if (rem > 0) quotient + 1 else quotient
    val page = if (req.page <= 0 || req.offset >= entities.size) 1 else req.page
    val offset = (page - 1) * req.size
    val entitiesPage = entities.slice(offset, offset + req.size)

    SearchResult(page, req.size, matchingPages, entities.size, entitiesPage)
  }

}
