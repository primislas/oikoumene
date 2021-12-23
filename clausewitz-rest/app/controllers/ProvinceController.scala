package controllers

import com.lomicron.eu4.repository.api.map.ProvinceSearchConf
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.ProvinceService
import utils.CCaseJSON

import scala.util.Try

/**
  * Frontend controller managing all static resource associate routes.
  *
  * @param cc Controller components reference.
  */
@Singleton
class ProvinceController @Inject()
(
  cc: ControllerComponents,
  provinceService: ProvinceService
) extends AbstractController(cc) {

  def findProvinces
  (
    page: Option[Int] = None,
    size: Option[Int] = None,

    name: Option[String] = None,
    owner: Option[String] = None,
    controller: Option[String] = None,
    core: Option[String] = None,

    religion: Option[String] = None,
    culture: Option[String] = None,

    religion_group: Option[String] = None,
    culture_group: Option[String] = None,

    area: Option[String] = None,
    region: Option[String] = None,
    superregion: Option[String] = None,
    continent: Option[String] = None,

    trade_good: Option[String] = None,
    trade_node: Option[String] = None,

    include_fields: Seq[String] = Seq.empty,
    exclude_fields: Seq[String] = Seq.empty,
    group_by: Option[String] = None,

    with_dictionary: Option[Boolean] = None,

  ): Action[AnyContent] = Action {

    val p = page.getOrElse(0)
    val s = size.getOrElse(10)

    val withDict = with_dictionary.getOrElse(false)
    val conf = ProvinceSearchConf(
      p, s, withDict, name, owner, controller, core,
      religion, religion_group, culture, culture_group,
      area, region, superregion, continent,
      trade_good, trade_node,
      include_fields.toSet, exclude_fields.toSet
    )
    val res = group_by match {
      case Some(group) => provinceService.groupProvinces(conf, group)
      case _ => provinceService.findProvinces(conf)
    }
    Ok(CCaseJSON.toJson(res))
  }

  def getProvince(id: String): Action[AnyContent] = Action {
    Try(id.toInt).toOption
      .map(provinceService.getProvince)
      .map(CCaseJSON.toJson)
      .map(Ok(_)).getOrElse(NotFound)
  }


}
