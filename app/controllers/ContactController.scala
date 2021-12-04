package controllers

import java.util.UUID

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import forms.ContactForm
import javax.inject.Inject
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc.{ Action, AnyContent, Request }
import utils.route.Calls

import scala.concurrent.{ ExecutionContext, Future }

/**
 * The `Sign Up` controller.
 */
class ContactController @Inject() (
  components: SilhouetteControllerComponents,
  contact: views.html.contact)(implicit ex: ExecutionContext)
  extends SilhouetteController(components) {

  /**
   * Views the `Sign Up` page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    Future.successful(Ok(contact(ContactForm.form)))
  }

  /**
   * Handles the submitted form.
   *
   * @return The result to display.
   */
  def submit: Action[AnyContent] = UnsecuredAction.async { implicit request: Request[AnyContent] =>
    ContactForm.form.bindFromRequest().fold(
      formWithErrors => {
        logger.debug("it had errors")
        // binding failure, you retrieve the form containing errors:
        Future.successful(BadRequest(contact(formWithErrors)))
      },
      contactData => {
        logger.debug("entered contactData processor")
        //val result = Redirect(routes.ContactController.view()).flashing("info" -> Messages("contact.ticket.received"))
        /* binding success, you get the actual value. */
        val newContactTicket = models.ContactTicket(
          id = UUID.randomUUID(),
          organization = Some(contactData.organization),
          email = Some(contactData.email),
          roleTitle = Some(contactData.roleTitle),
          firstName = Some(contactData.firstName),
          lastName = Some(contactData.lastName),
          subject = Some(contactData.subject),
          message = Some(contactData.message))
        for {
          contactTicket <- contactService.save(newContactTicket)
        } yield {
          mailerClient.send(Email(
            subject = contactData.subject,
            from = Messages("email.from"),
            to = Seq(contactData.email),
            bodyText = Some(views.txt.emails.contactForm(contactTicket).body),
            bodyHtml = Some(views.html.emails.contactForm(contactTicket).body)
          ))
          logger.debug("ticket sent to client")
          mailerClient.send(Email(
            subject = contactData.subject,
            from = Messages("email.from"),
            to = Seq(Messages("email.from")),
            bodyText = Some(views.txt.emails.supportContactForm(contactTicket).body),
            bodyHtml = Some(views.html.emails.supportContactForm(contactTicket).body)
          ))
          Redirect(routes.InfoController.view(Messages("contact.ticket.received"), Messages("contact.title")))
        }

      }
    )
  }
}

