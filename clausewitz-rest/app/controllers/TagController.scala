package controllers

import com.lomicron.oikoumene.repository.api.politics.TagSearchConf
import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import services.TagService
import utils.CCaseJSON

/**
  * Frontend controller managing all static resource associate routes.
  *
  * @param cc Controller components reference.
  */
@Singleton
class TagController @Inject()
(
  cc: ControllerComponents,
  tagService: TagService
) extends AbstractController(cc) {

  def findTags
  (
    page: Option[Int] = None,
    size: Option[Int] = None,

    id: Option[String] = None,
    name: Option[String] = None,

    primary_culture: Option[String] = None,
    religion: Option[String] = None,

    with_dictionary: Option[Boolean] = None,

  ): Action[AnyContent] = Action {

    val p = page.getOrElse(0)
    val s = size.getOrElse(10)

    val withDict = with_dictionary.getOrElse(false)
    val conf = TagSearchConf(p, s, withDict, id, name, primary_culture, religion)
    val res = tagService.findTags(conf)

    Ok(CCaseJSON.toJson(res))
  }

  def getTag(id: String): Action[AnyContent] = Action {
    Option(id)
      .map(tagService.getTag)
      .map(CCaseJSON.toJson)
      .map(Ok(_)).getOrElse(NotFound)
  }


}
