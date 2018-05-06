package models

import javax.inject.{Inject, Singleton}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewRepository @Inject()(dbConfigProvider: DatabaseConfigProvider, productRepository: ProductRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  import productRepository.ProductTable

  private val product_id = TableQuery[ProductTable]

  private class ReviewTable(tag: Tag) extends Table[Review](tag, "review") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

    def product = column[Long]("product")

    def comment = column[String]("comment")

    def product_fk = foreignKey("product_fk", product, product_id)(_.id)

    def * = (id, product, comment) <> ((Review.apply _).tupled, Review.unapply)
  }

  private val review = TableQuery[ReviewTable]

  def create(product: Long, comment: String): Future[Review] =
    db.run {
      (review.map(r => (r.product, r.comment))
      // Now define it to return the id, because we want to know what id was generated for the person
      returning review.map(_.id)
      // And we define a transformation for the returned value, which combines our original parameters with the
      // returned id
      into { case ((product, comment), id) => Review(id, product, comment) }
      // And finally, insert the person into the database
      ) += (product, comment)
  }

  def list(): Future[Seq[Review]] = db.run {
    review.result
  }
}
