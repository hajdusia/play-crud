package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import java.sql.Date

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, val basketIdsRepository: BasketIdsRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._


  class OrderTable(tag: Tag) extends Table[Order](tag, "order") {

    def order_id = column[Long]("order_id", O.PrimaryKey, O.AutoInc)

    def basket = column[Long]("basket")

    def date = column[Date]("date")

    def address = column[Long]("address")

    def basket_fk = foreignKey("basket_fk", basket, basket_id)(_.basket_id)

    def * = (order_id, basket, date, address) <> ((Order.apply _).tupled, Order.unapply)
  }

  import basketIdsRepository.BasketIdsTable

  private val basket_id = TableQuery[BasketIdsTable]


  private val order = TableQuery[OrderTable]

  def create(basket: Long, date: Date, address: Long): Future[Order] = db.run {
    (order.map(c => (c.basket, c.date, c.address))
      returning order.map(_.order_id)
      into { case ((basket, date, address), order_id) => Order(order_id, basket, date, address) }
      ) += (basket, date, address)
  }

  def list(): Future[Seq[Order]] = db.run {
    order.result
  }
}