package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }

@Singleton
class BasketIdsRepository @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]
  import dbConfig._
  import profile.api._


  class BasketIdsTable(tag: Tag) extends Table[BasketIds](tag, "basket_ids") {

    def basket_id = column[Long]("basket_id", O.PrimaryKey, O.AutoInc)
    def user = column[Long]("user")
    def * = (basket_id, user) <> ((BasketIds.apply _).tupled, BasketIds.unapply)
  }

  val basketIds = TableQuery[BasketIdsTable]


  def create(user: Long): Future[BasketIds] = db.run {
    (basketIds.map(p => (p.user))
      returning basketIds.map(_.basket_id)
      into ((user, basket_id) => BasketIds(basket_id, user))
      ) += (user)
  }

  def list(): Future[Seq[BasketIds]] = db.run {
    basketIds.result
  }
}
