package controllers

import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream

import com.lomicron.oikoumene.repository.api.RepositoryFactory
import javax.imageio.ImageIO
import javax.inject.{Inject, Singleton}
import play.api.mvc._


@Singleton
class GfxController @Inject
(
  cc: ControllerComponents,
  repos: RepositoryFactory,
) extends AbstractController(cc) {

  def getFlag(tag: String): Action[AnyContent] = Action {
    repos.gfx.findFlag(tag).map(toPngResult).getOrElse(NotFound)
  }

  def getTradeGood(id: String): Action[AnyContent] = Action {
    repos.gfx.findTradeGood(id).map(toPngResult).getOrElse(NotFound)
  }

  def getReligion(id: String): Action[AnyContent] = Action {
    repos.gfx.findReligion(id).map(toPngResult).getOrElse(NotFound)
  }

  def toPngResult(img: BufferedImage): Result = Ok(toPngBytes(img)).as("image/png")

  def toPngBytes(img: BufferedImage): Array[Byte] = toPng(img).toByteArray

  def toPng(img: BufferedImage): ByteArrayOutputStream = {
    val png = new ByteArrayOutputStream()
    ImageIO.write(img, "png", png)
    png
  }

}
