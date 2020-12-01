package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.{Inject, Singleton}
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}
import services.MapService

@Singleton
class MapController @Inject()
(
  cc: ControllerComponents,
  actorSystem: ActorSystem,
  materializer: Materializer,
  mapService: MapService
) extends AbstractController(cc) {

  implicit private val system: ActorSystem = actorSystem
  implicit private val mat: Materializer = materializer

  def socket: WebSocket = WebSocket.accept[String, String] {
    //noinspection ScalaUnusedSymbol
    request =>
      ActorFlow.actorRef { out =>
        MapSocketActor.props(out, mapService)
      }
  }

}
