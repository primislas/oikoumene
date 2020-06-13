package com.lomicron.oikoumene.parsers.map

import com.lomicron.oikoumene.model.Color
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.utils.collection.CollectionUtils.toOption

case class WorldMap
(
  mercator: MercatorMap,
  private val repos: RepositoryFactory
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


}

object WorldMap {

  def apply(mercator: MercatorMap, repos: RepositoryFactory): WorldMap = {
    val updated = addProvinceMeta(mercator.provinces, repos)
    new WorldMap(mercator.copy(provinces = updated), repos)
  }

  def addProvinceMeta(ps: Seq[Polygon], repos: RepositoryFactory): Seq[Polygon] = {
    val psByColor = repos.provinces.findAll.groupBy(_.color)

    ps
      .map(poly => psByColor
        .get(Color(poly.color))
        .flatMap(_.headOption)
        .map(p => poly.copy(provinceId = p.id))
        .getOrElse(poly)
      )
  }

}
