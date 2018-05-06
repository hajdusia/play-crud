package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._
import java.sql.Date

import scala.concurrent.ExecutionContext

class OrderController @Inject()(orderRepository: OrderRepository,
                                    cc: MessagesControllerComponents
                                   )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val orderForm: Form[CreateOrderForm] = Form {
    mapping(
      "basket" -> longNumber,
      "date" -> sqlDate,
      "address" -> longNumber
    )(CreateOrderForm.apply)(CreateOrderForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.order(orderForm))
  }

  def addOrder = Action { implicit request =>
    orderForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.order(formWithErrors))
      },
      order => {
        val orderId = orderRepository.create(order.basket, order.date, order.address)
        Redirect(routes.OrderController.getOrders()).flashing("success" -> "Order created!")
      }
    )

  }

  def getOrders = Action.async { implicit request =>
    orderRepository.list().map { basketIds =>
      Ok(Json.toJson(basketIds))
    }
  }
}


case class CreateOrderForm(basket: Long, date: Date, address: Long)