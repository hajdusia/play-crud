package models

/**
  * Description of class:
  *
  * Created on 28 Apr 2018 (16:24)
  *
  * @author dawid
  */

import java.sql.Date

import play.api.libs.json._

case class Order(order_id: Long, basket: Long, date: Date, address: Long)

object Order {
  implicit val orderFormat = Json.format[Order]
}

