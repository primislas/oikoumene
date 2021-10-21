package controllers

import akka.actor._
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.lomicron.utils.json.JsonMapper
import com.lomicron.utils.json.JsonMapper._
import com.typesafe.scalalogging.LazyLogging
import services.MapService

import scala.concurrent.Future


object Map3DSocketActor {
  def props(out: ActorRef, mapService: MapService): Props = Props(new Map3DSocketActor(out, mapService))
}

class Map3DSocketActor(out: ActorRef, mapService: MapService) extends Actor with LazyLogging {

  def receive: Receive = {
    case msg: String =>
      val cmdJson = JsonMapper.fromJson[JsonNode](msg)
      val cmd = toCommand(cmdJson)
      if (cmd.isEmpty) logger.error(s"Received an invalid message: $msg")
      else cmd.foreach(handleCommand)


      out ! msg
  }

  def toCommand(node: JsonNode): Option[WSMsg] =
    node
      .getString("event")
      .map(e => {
        logger.info(s"Handling a '$e' event")
        val data = node.getObject("data").getOrElse(JsonMapper.objectNode)
        WSMsg(e, data)
      })

  def handleCommand(cmd: WSMsg): Unit = {
    cmd.event match {
      case MapEvents.LoadMap => loadMap(out)
      case _ => logger.warn(s"Received an unexpected event: ${cmd.event}")
    }
  }

  def loadMap(sender: ActorRef): Unit = {
    import context.dispatcher
    def reply[T <: AnyRef](event: String, data: T): Unit = emitMessage(sender, event, data)
    def futureReply[T <: AnyRef](event: String, data: => T): Future[Unit] = Future { reply(event, data) }

    val meta = Future{ reply("mapMetadata", MapMetadata()) }
    val provinces = Future { reply("mapProvinceShapes", mapService.provinces) }
//    val rivers = Future { reply("mapRivers", mapService.rivers) }
//    val borders = Future { reply("mapBorders", mapService.borders) }
//    val names = Future { reply("mapNames", mapService.names) }
    val tags = futureReply("tagMetadata", mapService.tags)
    val provMeta = futureReply("provinceMetadata", mapService.provinceMeta)
  }

  def emitMessage[T <: AnyRef](sender: ActorRef, event: String, data: T): Unit = {
    sender ! toJson(Map("event" -> event, "data" -> data))
  }


}
