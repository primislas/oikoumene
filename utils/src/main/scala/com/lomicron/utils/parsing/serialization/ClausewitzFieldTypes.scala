package com.lomicron.utils.parsing.serialization

object ClausewitzFieldTypes {
  val ClzArray = "ClzArray"
  val ClzMultiLineArray = "ClzMultiLineArray"
  val ClzBoolean = "ClzBoolean"
  val ClzObject = "ClzObject"

  val arrayTypes = Set(ClzArray, ClzMultiLineArray)

  def isArray(ft: String): Boolean = arrayTypes.contains(ft)
}
