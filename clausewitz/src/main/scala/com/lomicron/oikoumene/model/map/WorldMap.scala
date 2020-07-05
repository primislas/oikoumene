package com.lomicron.oikoumene.model.map

import com.lomicron.oikoumene.model.provinces.Province
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.repository.api.map.ProvinceRepository
import com.lomicron.utils.collection.CollectionUtils.SeqEx
import com.lomicron.utils.geometry.{Polygon, SphericalCoord, SphericalMap}

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
  def lakes: Seq[ElevatedLake] = repos.geography.map.lakes

  def ownerGroups: Seq[Seq[Province]] =
    repos.provinces.findAll
      .filter(_.state.owner.isDefined)
      .groupBy(_.state.owner.get)
      .values.toList
      .flatMap(provinceGroups)

  def provinceGroups(ps: Seq[Province]): Seq[Seq[Province]] = {
    val routes = repos.geography.map.routes

    var groups = Seq.empty[Seq[Province]]
    var remainingPs = ps
    while (remainingPs.nonEmpty) {
      val gHead = remainingPs.head
      var g = Seq(gHead)
      var gIds = Set(gHead.id)
      var neighbors = sameOwnerNeighbors(gHead, routes, repos.provinces)
        .filterNot(p => gIds.contains(p.id))

      while (neighbors.nonEmpty) {
        g = g ++ neighbors
        gIds = gIds ++ neighbors.map(_.id)
        neighbors = neighbors
          .flatMap(sameOwnerNeighbors(_, routes, repos.provinces))
          .filterNot(p => gIds.contains(p.id))
      }

      groups = groups :+ g
      remainingPs = remainingPs.filterNot(p => gIds.contains(p.id))

    }

    groups
  }

  def sameOwnerNeighbors
  (
    p: Province,
    r: Map[Int, Seq[Route]],
    provinces: ProvinceRepository,
  )
  : Seq[Province] =
    r.getOrElse(p.id, Seq.empty)
      .map(_.to)
      .flatMap(provinces.find(_).toOption)
      .filter(_.state.owner == p.state.owner)
      .distinctBy(_.id)

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
