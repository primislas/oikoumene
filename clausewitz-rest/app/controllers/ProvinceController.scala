package controllers

import com.lomicron.oikoumene.repository.api.map.ProvinceSearchConf
import com.lomicron.utils.json.JsonMapper
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.ProvinceService

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
    owner: Option[String] = None,
    controller: Option[String] = None,
    core: Option[String] = None,

    religion: Option[String] = None,
    culture: Option[String] = None,

    religion_group: Option[String],
    culture_group: Option[String],

    area: Option[String],
    region: Option[String],
    superregion: Option[String],
    continent: Option[String],

    trade_good: Option[String],
    trade_node: Option[String],

    include_fields: Seq[String],
    exclude_fields: Seq[String],
    group_by: Option[String],
  ): Action[AnyContent] = Action {

    val p = page.getOrElse(0)
    val s = size.getOrElse(10)

    val conf = ProvinceSearchConf(p, s, owner, controller, core,
      religion, religion_group, culture, culture_group,
      area, region, superregion, continent,
      trade_good, trade_node,
      include_fields.toSet, exclude_fields.toSet)
    val res = group_by match {
      case Some(group) => provinceService.groupProvinces(conf, group)
      case _ => provinceService.findProvinces(conf)
    }
    Ok(JsonMapper.toJson(res))
  }

  def getProvince(id: String): Action[AnyContent] = Action {
    Try(id.toInt).toOption
      .map(provinceService.getProvince)
      .map(JsonMapper.toJson)
      .map(Ok(_)).getOrElse(NotFound)
  }


}
