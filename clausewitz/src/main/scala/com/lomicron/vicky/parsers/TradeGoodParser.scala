package com.lomicron.vicky.parsers

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields.idKey
import com.lomicron.oikoumene.parsers.{ClausewitzParser, ConfigField}
import com.lomicron.oikoumene.repository.api.resources.LocalisationRepository
import com.lomicron.utils.collection.CollectionUtils._
import com.lomicron.utils.json.JsonMapper._
import com.lomicron.vicky.model.production.TradeGood
import com.lomicron.vicky.repository.api.{RepositoryFactory, ResourceRepository, TradeGoodRepository}
import com.typesafe.scalalogging.LazyLogging

import scala.collection.immutable.ListMap

object TradeGoodParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false): TradeGoodRepository =
    apply(repos.resources, repos.localisations, repos.tradeGoods, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   tradeGoodRepo: TradeGoodRepository,
   evalEntityFields: Boolean): TradeGoodRepository = {

    val tgFiles = files.getTradeGoods
    val tradeGoods = ClausewitzParser
      .parseFileFieldsAsEntities(tgFiles)
      .flatMap(parseTradeGoods)
      .map(localisation.setLocalisation)

    if (evalEntityFields)
      ConfigField.printCaseClass("TradeGood", tradeGoods)

    tradeGoods.map(TradeGood.fromJson).foreach(tradeGoodRepo.create)

    tradeGoodRepo
  }

  private def parseTradeGoods(tradeGoodCategory: ObjectNode): Seq[ObjectNode] =
    tradeGoodCategory.fields.toStream
      .map(e => e.getKey -> e.getValue).foldLeft(ListMap[String, JsonNode]())(_ + _)
      .flatMapValues(tradeGood => tradeGood.asObject)
      .mapKVtoValue((id, tradeGood) => tradeGood.setEx(idKey, id))
      .values
      .map(_.setEx("category", tradeGoodCategory.get("id")))
      .toSeq

}
