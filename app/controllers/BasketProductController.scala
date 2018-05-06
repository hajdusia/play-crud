package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class BasketProductController @Inject()(basketProductRepository: BasketProductRepository,
                                    cc: MessagesControllerComponents
                                   )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val basketProductForm: Form[CreateBasketProductForm] = Form {
    mapping(
      "basket" -> longNumber,
      "product" -> longNumber,
      "amount" -> number
    )(CreateBasketProductForm.apply)(CreateBasketProductForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.basketproduct(basketProductForm))
  }


  def addBasketProduct = Action { implicit request =>
    basketProductForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.basketproduct(formWithErrors))
      },
      basketProduct => {
        val basketId = basketProductRepository.create(basketProduct.basket, basketProduct.product, basketProduct.amount)
        Redirect(routes.BasketProductController.getBasketProducts()).flashing("success" -> "Contact saved!")
      }
    )

  }

  def getBasketProducts = Action.async { implicit request =>
    basketProductRepository.list().map { basketIds =>
      Ok(Json.toJson(basketIds))
    }
  }
}

case class CreateBasketProductForm(basket: Long, product: Long, amount: Int)
