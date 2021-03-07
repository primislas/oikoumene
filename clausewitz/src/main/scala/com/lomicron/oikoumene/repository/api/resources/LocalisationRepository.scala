package com.lomicron.oikoumene.repository.api.resources

import com.fasterxml.jackson.databind.node.{ObjectNode, TextNode}
import com.lomicron.oikoumene.model.localisation.LocalisationEntry
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper.{ObjectNodeEx, patchFieldValue}

trait LocalisationRepository {

  def upsertEntries(es: Seq[LocalisationEntry]): LocalisationRepository

  def findEntry(key: String): Option[LocalisationEntry]

  def fetchAllEntries: Seq[LocalisationEntry]

  def searchEntries(keyPattern: String): Seq[LocalisationEntry]

  def findTag(key: String): Option[ObjectNode]

  def fetchTags: Map[String, ObjectNode]

  def fetchProvince(provId: Int): Option[ObjectNode]

  def fetchProvinces: Map[Int, ObjectNode]

  def findAndSetAsLocName(key: String, target: ObjectNode): ObjectNode =
    findEntry(key)
      .map(_.text)
      .map(TextNode.valueOf)
      .map(name => {
        if (target.has("localisation"))
          patchFieldValue(target.get("localisation").asInstanceOf[ObjectNode], "name", name)
        else {
          val l = patchFieldValue(JsonMapper.objectNode, "name", name)
          target.set("localisation", l)
        }
        target
      })
      .getOrElse(target)


  /**
    * Looks for localisation entry by default field "id"
    * and sets it to "localisation" field if found.
    *
    * @param o object that might have localisation
    * @return object with localisation field if localisation is found
    */
  def setLocalisation(o: ObjectNode): ObjectNode =
    setLocalisationByField(o, Fields.idKey)

  /**
    * Looks for localisation entry by id taken from provided
    * objects field, and sets it to "localisation" field if found.
    *
    * @param o     object that might have localisation
    * @param field object's field with localisation id
    * @return object with localisation field if localisation is found
    */
  def setLocalisationByField(o: ObjectNode, field: String): ObjectNode =
    o.getString(field).map(id => findAndSetAsLocName(id, o)).getOrElse(o)

  def setBuildingLocalisation(o: ObjectNode): ObjectNode =
    o.getString(Fields.idKey)
      .map(id => s"building_$id")
      .map(findAndSetAsLocName(_, o))
      .getOrElse(o)

}
