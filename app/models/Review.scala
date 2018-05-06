package models

import play.api.libs.json._

case class Review(id: Long, product: Long, comment: String)

object Review {
  implicit val opinionFormat = Json.format[Review]
}

