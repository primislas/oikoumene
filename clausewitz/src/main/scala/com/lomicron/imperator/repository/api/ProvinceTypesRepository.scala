package com.lomicron.imperator.repository.api

trait ProvinceTypesRepository { self =>

  def typeOfProvince(id: Int): Option[String]

  def add(provId: Int, provType: String): ProvinceTypesRepository

  def add(provIds: Seq[Int], provType: String): ProvinceTypesRepository = {
    provIds.foreach(add(_, provType))
    self
  }

}
