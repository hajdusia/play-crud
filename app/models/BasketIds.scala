package models

import play.api.libs.json._

case class BasketIds(basket_id: Long, user: Long)

object BasketIds {
  implicit val basketIdsFormat = Json.format[BasketIds]
}
