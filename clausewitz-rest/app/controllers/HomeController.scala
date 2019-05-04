package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

@Singleton
class HomeController @Inject()(cc: ControllerComponents) extends AbstractController(cc) {

  def appSummary: Action[AnyContent] = Action {
    Ok("""{"content":"Scala Play Angular Seed"}""")
  }

}
