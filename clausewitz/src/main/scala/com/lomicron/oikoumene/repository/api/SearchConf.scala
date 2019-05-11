package com.lomicron.oikoumene.repository.api

trait SearchConf {
  /**
    * Searched page. In SQL terms offset = page * size.
    */
  val page: Int = 0
  /**
    * Search page size.
    */
  val size: Int = 10
  /**
    * Configure if dictionary should be included.
    */
  val withDictionary: Boolean = false

  def offset: Int = (page - 1) * size

}
