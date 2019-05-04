package com.lomicron.oikoumene.repository.api

case class SearchResult[T]
(
  page: Int,
  size: Int,
  totalPages: Int,
  totalEntities: Int,
  entities: Seq[T]
)

object SearchResult {

  def apply[T](page: Int, size: Int, totalEntities: Int, entities: Seq[T]): SearchResult[T] = {
    val quotient = totalEntities / size
    val remainder = totalEntities % size
    val totalPages = if (remainder == 0) quotient else quotient + 1
    SearchResult(page, size, totalPages, totalEntities, entities)
  }

}