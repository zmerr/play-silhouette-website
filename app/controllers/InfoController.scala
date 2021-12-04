package controllers

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc._
import utils.auth.{ DefaultEnv, WithProvider }

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.duration.DurationInt
import scala.concurrent.{ Await, ExecutionContext }
import scala.language.postfixOps

/**
 * The Info controller.
 */
class InfoController @Inject() (
  scc: SilhouetteControllerComponents,
  info: views.html.info
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Views the Info page.
   *
   * @return The result to display.
   */
  def view(message: String, title: String): Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)) {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      Ok(info(request.identity, message, title))
  }

}
