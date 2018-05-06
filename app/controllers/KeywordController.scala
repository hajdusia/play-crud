package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms._
import play.api.libs.json.Json
import play.api.mvc._

import scala.concurrent.ExecutionContext

class KeywordController @Inject()(keywordRepository: KeywordRepository,
                                  cc: MessagesControllerComponents
                                 )(implicit ec: ExecutionContext)
  extends MessagesAbstractController(cc) {

  val keywordForm: Form[CreateKeywordForm] = Form {
    mapping(
      "value" -> nonEmptyText
    )(CreateKeywordForm.apply)(CreateKeywordForm.unapply)
  }

  def index = Action { implicit request =>
    Ok(views.html.keyword(keywordForm))
  }

  def addKeyword = Action { implicit request =>
    keywordForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.keyword(formWithErrors))
      },
      keyword => {
        val keywordId = keywordRepository.create(keyword.value)
        Redirect(routes.KeywordController.getKeywords()).flashing("success" -> "Keyword saved!")
      }
    )
  }

  def getKeywords = Action.async { implicit request =>
    keywordRepository.list().map { basketIds =>
      Ok(Json.toJson(basketIds))
    }
  }
}

case class CreateKeywordForm(value: String)