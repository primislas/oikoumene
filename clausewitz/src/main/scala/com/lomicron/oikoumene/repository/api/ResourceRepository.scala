package com.lomicron.oikoumene.repository.api

import com.lomicron.oikoumene.model.localisation.LocalisationEntry

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

  def getLocalisation(): Seq[LocalisationEntry] =
    getLocalisation(SupportedLanguages.english)

  def getLocalisation(language: String): Seq[LocalisationEntry]

  object SupportedLanguages {
    val english = "english"
    val german = "german"
    val spanish = "spanish"
    val french = "french"
  }
}