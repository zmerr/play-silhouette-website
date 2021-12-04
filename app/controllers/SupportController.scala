package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.{ SilhouetteController, SilhouetteControllerComponents, routes }
import forms.{ ChangePasswordForm, SupportForm }
import javax.inject.Inject
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc._
import utils.auth.{ DefaultEnv, WithProvider }
import views.html.emails.supportForm

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The Support controller.
 */
class SupportController @Inject() (
  scc: SilhouetteControllerComponents,
  support: views.html.support
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Views the `Change Password` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)) {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      Ok(support(SupportForm.form, request.identity))
  }

  /**
   * Changes the password.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)).async {
    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      SupportForm.form.bindFromRequest().fold(
        badform => Future.successful(BadRequest(support(badform, request.identity))),
        supportData => {
          val newSupportTicket = models.SupportTicket(
            id = UUID.randomUUID(),
            userID = request.identity.userID,
            organization = request.identity.organization,
            email = request.identity.email,
            firstName = request.identity.firstName,
            lastName = request.identity.lastName,
            subject = Some(supportData.subject),
            message = Some(supportData.message),
            roleTitle = request.identity.roleTitle)
          for {
            supportTicket <- supportService.save(newSupportTicket)
          } yield {
            mailerClient.send(Email(
              subject = supportData.subject,
              from = Messages("email.from"),
              to = Seq(request.identity.email.get),
              bodyText = Some(views.txt.emails.supportForm(supportTicket).body),
              bodyHtml = Some(views.html.emails.supportForm(supportTicket).body)
            ))
            logger.debug("ticket sent to client")
            mailerClient.send(Email(
              subject = supportData.subject,
              from = Messages("email.from"),
              to = Seq(Messages("email.from")),
              bodyText = Some(views.txt.emails.supportSupportForm(supportTicket).body),
              bodyHtml = Some(views.html.emails.supportSupportForm(supportTicket).body)
            ))
            Redirect(routes.InfoController.view(Messages("support.ticket.received"), Messages("support.title")))
          }
        }
      )
  }
}
