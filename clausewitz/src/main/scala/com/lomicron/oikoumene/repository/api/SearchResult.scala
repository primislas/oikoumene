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

}
