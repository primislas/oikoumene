package com.lomicron.oikoumene.repository.api

trait ResourceRepository {

  /**
    *
    * @return country files by country tags
    */
  def getCountryTags: Map[String, String]

  /**
    *
    * @return country file content by country tag
    */
  def getCountries(filesByTags: Map[String, String]): Map[String, String]

  /**
    *
    *
    * @return country history by country tag
    */
  def getCountryHistory: Map[String, String]

  /**
    *
    * @return country
    */
  def getCountryNames: Map[String, String]
}
