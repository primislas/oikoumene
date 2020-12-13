package com.lomicron.oikoumene.model.government

import scala.collection.immutable.ListSet

case class ReformLevel(id: String, reforms: ListSet[String] = ListSet.empty)
