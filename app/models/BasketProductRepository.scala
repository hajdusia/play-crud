package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BasketProductRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, basketIdsRepository: BasketIdsRepository, productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._

  import basketIdsRepository.BasketIdsTable

  private val basket_id = TableQuery[BasketIdsTable]

  import productRepository.ProductTable

  private val product_id = TableQuery[ProductTable]

  private class BasketProductTable(tag: Tag) extends Table[BasketProduct](tag, "basket_product") {

    def basket = column[Long]("basket", O.PrimaryKey)
    def product = column[Long]("product")
    def amount = column[Int]("amount")
    def basket_fk = foreignKey("basket_fk", basket, basket_id)(_.basket_id)
    def product_fk = foreignKey("produck_fk", product, product_id)(_.id)
    def * = (basket, product, amount) <> ((BasketProduct.apply _).tupled, BasketProduct.unapply)
  }

  private val basketProduct = TableQuery[BasketProductTable]


  def create(basket: Long, product: Long, amount: Int): Future[Int] =
    db.run(basketProduct += BasketProduct(basket, product, amount))

  def list(): Future[Seq[BasketProduct]] = db.run {
    basketProduct.result
  }
}
