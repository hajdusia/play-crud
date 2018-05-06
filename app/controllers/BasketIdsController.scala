package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class BasketIdsController @Inject()(basketIdsRepository: BasketIdsRepository,
                                    cc: MessagesControllerComponents
                                   )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {


  val basketIdsForm: Form[CreateBasketIdsForm] = Form {
    mapping(
      "user" -> longNumber
    )(CreateBasketIdsForm.apply)(CreateBasketIdsForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.basketid(basketIdsForm))
  }


  def addBasketId = Action { implicit request =>
    basketIdsForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.basketid(formWithErrors))
      },
      basket => {
        val basketId = basketIdsRepository.create(basket.user)
        Redirect(routes.BasketIdsController.getBasketIds()).flashing("success" -> "Contact saved!")
      }
    )

  }

  def getBasketIds = Action.async { implicit request =>
    basketIdsRepository.list().map { basketIds =>
      Ok(Json.toJson(basketIds))
    }
  }
}

case class CreateBasketIdsForm(user: Long)
