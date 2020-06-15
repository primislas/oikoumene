package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils.toOption

case class WorldMap
(
  mercator: MercatorMap,
  private val repos: RepositoryFactory,
) {

  private var sphere: Option[SphericalMap] = None

  private def getSphere: SphericalMap =
    sphere.getOrElse({
      val s = mercator.toSphere
      sphere = Option(s)
      s
    })

  def rotate(rotation: Option[SphericalCoord] = None): Seq[Polygon] =
    rotation
      .map(getSphere.rotate)
      .getOrElse(getSphere)
      .project

  def rivers: Seq[River] = repos.geography.map.rivers

}

object WorldMap {

  def apply(
             mercator: MercatorMap,
             repos: RepositoryFactory
           ): WorldMap = {

    val withProvIds = addProvinceMeta(mercator, repos)
    new WorldMap(withProvIds, repos)
  }

  def addProvinceMeta(m: MercatorMap, repos: RepositoryFactory): MercatorMap = {
    val psByColor = repos.provinces.findAll.groupBy(_.color.toInt)

    val withProvIds = m.provinces
      .map(shape =>
        if (shape.provColor.isDefined)
          psByColor
            .get(shape.provColor.get)
            .flatMap(_.headOption)
            .map(p => shape.withProvinceId(p.id))
            .getOrElse(shape)
        else shape
      )

    m.copy(provinces = withProvIds)
  }

}
