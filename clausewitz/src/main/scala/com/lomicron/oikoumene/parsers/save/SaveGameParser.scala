package com.lomicron.oikoumene.parsers.save

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.{IntNode, ObjectNode, TextNode}
import com.lomicron.oikoumene.model.save.GamestateSave
import com.lomicron.oikoumene.parsers.ClausewitzParser
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, _}
import com.typesafe.scalalogging.LazyLogging

import scala.util.Try

object SaveGameParser extends LazyLogging {

  val provincesKey = "provinces"
  val tagsKey = "countries"

  def apply(gamestate: String): GamestateSave = {
    logger.info("Parsing save game...")
    val (save, errors) = ClausewitzParser.parse(gamestate)
    errors.foreach(pe => logger.error(pe.message))

    val provinces = parseProvinces(save)
    val countries = parseTags(save)

    val state = GamestateSave.fromJson(save)
    logger.info("Parsed save game")
    state
  }

  def parseTags(save: ObjectNode): Seq[ObjectNode] = {
    val tags = save
      .getObject("countries")
      .getOrElse(objectNode)
      .entries()
      .map(setTagId)
      .map(prepTagHistory)
    save.setEx(tagsKey, tags)

    tags
  }

  def setTagId(kv: (String, JsonNode)): ObjectNode = {
    val (k, tagNode) = kv
    val tag = tagNode.asObject.getOrElse(objectNode)
    tag.setEx("id", TextNode.valueOf(k))
  }

  def prepTagHistory(t: ObjectNode): ObjectNode =
    t
      .getObject("history")
      .map(ClausewitzParser.parseHistory)
      .map(t.setEx("history", _))
      .getOrElse(t)

  def parseProvinces(save: ObjectNode): Seq[ObjectNode] = {
    val provs = save
      .getObject("provinces")
      .getOrElse(objectNode)
      .entries()
      .flatMap(kv => {
        val (k, pn) = kv
        val idOpt = Try(k.toInt * -1).toOption
        val pOpt = pn.asObject
        for {
          id <- idOpt
          p <- pOpt
        } yield p.setEx("id", IntNode.valueOf(id))
      })
      .map(cleanUpProvince)
    save.setEx(provincesKey, provs)
    provs
  }

  def cleanUpProvince(p: ObjectNode): ObjectNode = {
    // Cleaning up empty discovered_by objects
    p.getObject("discovered_by")
      .filter(_.isEmpty())
      .foreach(_ => p.removeEx("discovered_by"))
    p
  }

}
