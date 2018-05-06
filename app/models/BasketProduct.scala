package models

import play.api.libs.json._

case class BasketProduct(basket: Long, product: Long, amount: Int)

object BasketProduct {
  implicit val basketProductFormat = Json.format[BasketProduct]
}
