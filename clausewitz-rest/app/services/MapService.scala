package services

import com.lomicron.oikoumene.model.map.{MapModes, WorldMap}
import com.lomicron.oikoumene.repository.api.RepositoryFactory
import com.lomicron.oikoumene.service.map.{MapBuilderSettings, SvgMapService, SvgMapStyles}
import com.lomicron.oikoumene.service.svg.{SvgElement, SvgTags}
import com.lomicron.oikoumene.tools.map.MapBuilder
import com.lomicron.oikoumene.tools.model.metadata.ProvinceMetadata

import javax.inject.{Inject, Singleton}
import scala.collection.immutable.ListMap

@Singleton
class MapService @Inject
(repos: RepositoryFactory)
{
  private val service = SvgMapService(repos)
  private val world = WorldMap(repos)
  type JsonMap = Map[String, Any]

  def provinces: Seq[Map[String, AnyRef]] = world
    .mercator
    .provinces
    .map(p => ListMap(
      "provId" -> p.provId,
      "path" -> p.path.map(_.reflectY(2048)),
      "clip" -> p.clip.map(_.path).map(_.map(_.reflectY(2048))),
    ))

  def fetchMapSvg: String = {
      MapBuilder.buildMap(repos)
  }

  def style: String = {
    val settings = MapBuilderSettings(None, MapModes.POLITICAL, Some(true), Some(true), Some(true))
    SvgMapStyles
      .styleOf(settings, repos)
      .customContent.getOrElse("")
  }

  def svgProvinces: Seq[Map[String, AnyRef]] =
    world.mercator
      .provinces
      .map(service.provinceToSvg(_, MapModes.PROVINCE_OUTLINE))
      .map(p => ListMap(
        "provinceId" -> p.id.map(_.toInt),
        "classes" -> p.classes,
        "path" -> p.path
      ))

  def rivers: Seq[SvgElement] =
    world.mercator.rivers.flatMap(service.riverToSvg(_))

  def svgBorders: Seq[Map[String, AnyRef]] = {
    val bordersWithType = world.mercator.borders.map(service.setBorderType)
    service.borderSvg(bordersWithType).children
      .filter(_.children.nonEmpty)
      .flatMap(bGroup => {
        if (bGroup.children.headOption.exists(_.tag == SvgTags.PATH)) Seq(bGroup)
        else bGroup.children.map(cGroup => cGroup.copy(classes = bGroup.classes ++ cGroup.classes))
      })
      .map(bGroup => {
        Map(
          "id" -> bGroup.id,
          "classes" -> bGroup.classes,
          "paths" -> bGroup.children.map(_.path).filter(_.isDefined)
        )
      })
  }

  def borders: Seq[Map[String, AnyRef]] = {
    world
      .mercator
      .borders
      .map(service.setBorderType)
      .map(b => ListMap(
        "paths" -> b.path,
        "type" -> b.`type`,
      ))
  }

  def names: Seq[Map[String, Any]] =
    service.nameSvg(world).children
      .grouped(2)
      .map(name => {
        val path = name.head
        val text = name.last
        val textPath = text.children.head

        Map(
          "id" -> path.id,
          "classes" -> text.classes,
          "path" -> path.path,
          "name" -> textPath.customContent,
          "fontSize" -> textPath.fontSize,
          "textLength" -> textPath.textLength,
        )
      })
      .toList

  def tags: Seq[JsonMap] =
    repos
      .tags
      .findAll
      .map(t => ListMap(
        "id" -> t.id,
        "name" -> t.name,
        "color" -> t.color
      ))

  def provinceMeta: Seq[ProvinceMetadata] =
    repos
      .provinces
      .findAll
      .map(ProvinceMetadata(_))

}

case class River(path: String, classes: Seq[String] = Seq.empty)
