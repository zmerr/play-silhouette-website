package controllers

import java.net.URLDecoder
import java.util.UUID
import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc.{ Action, AnyContent, Request }
import utils.route.Calls

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Activate Account` controller.
 */
class ActivateAccountController @Inject() (
  scc: SilhouetteControllerComponents
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Sends an account activation email to the user with the given email.
   *
   * @return The result to display.
   */
  def send(idString: String, domainString: String): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    val id = idString.replaceAll("____", "\\.")
    val domain = domainString.replaceAll("____", "\\.")
    val email = id + "@" + domain
    val decodedEmail = URLDecoder.decode(email, "UTF-8")
    val loginInfo = LoginInfo(CredentialsProvider.ID, decodedEmail)
    val result = Redirect(Calls.signin).flashing("info" -> Messages("activation.email.sent", id, domain))

    userService.retrieve(loginInfo).flatMap {
      case Some(user) =>
        if (user.activated) Future.successful(result)
        else {
          authTokenService.create(user.userID).map { authToken =>
            val url = routes.ActivateAccountController.activate(authToken.id).absoluteURL(secure = true)

            mailerClient.send(Email(
              subject = Messages("email.activate.account.subject"),
              from = Messages("email.from"),
              to = Seq(decodedEmail),
              bodyText = Some(views.txt.emails.activateAccount(user, url).body),
              bodyHtml = Some(views.html.emails.activateAccount(user, url).body)
            ))
            result
          }
        }
      case None => Future.successful(result)
    }
  }

  /**
   * Activates an account.
   *
   * @param token The token to identify a user.
   * @return The result to display.
   */
  def activate(token: UUID): Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    authTokenService.validate(token).flatMap {
      case Some(authToken) => userService.retrieve(authToken.userID).flatMap {
        case Some(user) if user.loginInfo.providerID == CredentialsProvider.ID =>
          userService.save(user.copy(activated = true)).map { _ =>
            Redirect(Calls.signin).flashing("success" -> Messages("account.activated"))
          }
        case _ => Future.successful(Redirect(Calls.signin).flashing("error" -> Messages("invalid.activation.link")))
      }
      case None => Future.successful(Redirect(Calls.signin).flashing("error" -> Messages("invalid.activation.link")))
    }
  }
}
