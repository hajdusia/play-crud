package models

import java.sql.Date

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, basketIdsRepository: BasketIdsRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import basketIdsRepository.BasketIdsTable

  private val basket_id = TableQuery[BasketIdsTable]

  private class PaymentTable(tag: Tag) extends Table[Payment](tag, "payment") {

    def payment_id = column[Long]("payment_id", O.PrimaryKey, O.AutoInc)

    def basket = column[Long]("basket")

    def value = column[Float]("value")

    def date = column[Date]("date")


    def basket_fk = foreignKey("basket_fk", basket, basket_id)(_.basket_id)

    def * = (payment_id, basket, value, date) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  private val payment = TableQuery[PaymentTable]

  def create(basket: Long, value: Float, date: Date): Future[Payment] = db.run {
    (payment.map(c => (c.basket, c.value, c.date))
      returning payment.map(_.payment_id)
      into { case ((basket, value, date), payment_id) => Payment(payment_id, basket, value, date)}
      ) += (basket, value, date)
  }

  def list(): Future[Seq[Payment]] = db.run {
    payment.result
  }
}