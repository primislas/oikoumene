package com.lomicron.oikoumene.parsers.provinces

import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.oikoumene.model.map.TerrainMapColorConf
import com.lomicron.oikoumene.model.provinces.Terrain
import com.lomicron.oikoumene.parsers.ClausewitzParser.Fields._
import com.lomicron.oikoumene.parsers.ClausewitzParser.{fieldsToObjects, parseFilesAsEntities, setLocalisation}
import com.lomicron.oikoumene.parsers.ConfigField
import com.lomicron.oikoumene.repository.api.map.GeographicRepository
import com.lomicron.oikoumene.repository.api.resources.{LocalisationRepository, ResourceRepository}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging

object TerrainParser extends LazyLogging {

  def apply(repos: RepositoryFactory,
            evalEntityFields: Boolean = false)
  : GeographicRepository =
    apply(repos.resources, repos.localisations, repos.geography, evalEntityFields)

  def apply
  (files: ResourceRepository,
   localisation: LocalisationRepository,
   geography: GeographicRepository,
   evalEntityFields: Boolean)
  : GeographicRepository = {

    val confs = files.getTerrain
    val cs = parseFilesAsEntities(confs)

    val terrainColorConfs = cs
      .flatMap(_.getObject(terrainKey))
      .flatMap(parseTerrainMapConf)

    val terrainCategories = cs
      .filter(hasTerrainCategories)
      .flatMap(_.getObject(terrainCategoriesKey))
      .flatMap(fieldsToObjects(_, idKey))
      .map(renameField(_, terrainProvincesKey, provinceIdsKey))
      .map(setLocalisation(_, localisation))

    if (evalEntityFields)
      ConfigField.printCaseClass("Terrain", terrainCategories)

    terrainCategories.map(Terrain.fromJson).foreach(geography.terrain.create)
    val colorConfs = terrainColorConfs.map(TerrainMapColorConf.fromJson)
    geography.map.setTerrainMapColorConf(colorConfs)

    geography
  }

  private def hasTerrainCategories(o: ObjectNode): Boolean =
    if (!o.has(terrainCategoriesKey)) {
      logger.warn("Found no terrain categories")
      false
    } else if (!o.get(terrainCategoriesKey).isObject) {
      logger.warn(s"Expected terrain categories to be declared as JSON object, instead encountered: ${o.get(terrainCategoriesKey).toString}")
      false
    } else true

  private def parseTerrainMapConf(o: ObjectNode): Seq[ObjectNode] =
    fieldsToObjects(o, idKey).map(parseTerrainMapConfColor)

  private def parseTerrainMapConfColor(conf: ObjectNode): ObjectNode =
    conf
      .getArray("color")
      .map(_.toSeq)
      .flatMap(_.headOption)
      .map(c => conf.setEx("color", c))
      .getOrElse(conf)

}
