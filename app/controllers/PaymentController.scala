package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats.floatFormat
import play.api.libs.json.Json
import play.api.mvc._
import java.sql.Date

import scala.concurrent.ExecutionContext

class PaymentController @Inject()(paymentRepository: PaymentRepository,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val paymentForm: Form[CreatePaymentForm] = Form {
    mapping(
      "basket" -> longNumber,
      "value" -> of(floatFormat),
      "date" -> sqlDate
    )(CreatePaymentForm.apply)(CreatePaymentForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.payment(paymentForm))
  }

  def addPayment = Action { implicit request =>
    paymentForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.payment(formWithErrors))
      },
      payment => {
        val paymentid = paymentRepository.create(payment.basket, payment.value, payment.date)
        Redirect(routes.PaymentController.getPayments()).flashing("success" -> "Payment saved!")
      }
    )

  }

  def getPayments = Action.async { implicit request =>
    paymentRepository.list().map { basketIds =>
      Ok(Json.toJson(basketIds))
    }
  }
}


case class CreatePaymentForm(basket: Long, value: Float, date: Date)