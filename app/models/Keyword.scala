package models

import play.api.libs.json._

case class Keyword(keyword_id: Long, value: String)

object Keyword {
  implicit val keywordFormat = Json.format[Keyword]
}

