package controllers

import java.util.UUID
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import forms.ContactForm

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc.{ AbstractController, Action, AnyContent, ControllerComponents, Request }
import utils.route.Calls

import scala.concurrent.{ ExecutionContext, Future }

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
class HomeController @Inject() (
  components: SilhouetteControllerComponents,
  indexView: views.html.index)(implicit ex: ExecutionContext) extends SilhouetteController(components) {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index(): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(indexView()))
  }

}
