package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.{Failure, Success}

class ReviewController @Inject()(opinionRepository: ReviewRepository, productsRepo: ProductRepository,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val reviewForm: Form[CreateReviewForm] = Form {
    mapping(
      "product" -> longNumber,
      "comment" -> nonEmptyText
    )(CreateReviewForm.apply)(CreateReviewForm.unapply)
  }

  def index = Action.async { implicit request =>
    val products = productsRepo.list()
    products.map(prod => Ok(views.html.review(reviewForm, prod)))
  }

  def addReview = Action.async { implicit request =>
    var a:Seq[Product] = Seq[Product]()
    val products = productsRepo.list().onComplete{
      case Success(prod) => a= prod
      case Failure(_) => print("fail")
    }

    reviewForm.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(
          Ok(views.html.review(formWithErrors, a))
        )
      },
      review => {
        opinionRepository.create(review.product, review.comment).map { _ =>
          Redirect(routes.ReviewController.getReviews).flashing("success" -> "Opinion saved!")
        }
      }
    )

  }

  def getReviews = Action.async { implicit request =>
    opinionRepository.list().map { opinion =>
      Ok(Json.toJson(opinion))
    }
  }
}


case class CreateReviewForm(product: Long, comment: String)