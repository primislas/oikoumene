package com.lomicron.oikoumene.repository.api

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
    val totalPages = if (remainder == 0) quotient else quotient + 1
    SearchResult(page, size, totalPages, totalEntities, entities)
  }

  def apply[T](req: SearchConf, entities: Seq[T]): SearchResult[T] = {
    val quotient = entities.size / req.size
    val rem = entities.size % req.size
    val matchingPages = if (rem > 0) quotient + 1 else quotient
    val page = if (req.offset < entities.size) req.page else 1
    val offset = page * req.size
    val entitiesPage = entities.slice(offset, offset + req.size)

    SearchResult(page, req.size, matchingPages, entities.size, entitiesPage)
  }

}
