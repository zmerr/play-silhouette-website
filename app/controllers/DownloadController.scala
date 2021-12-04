package controllers

import java.util.UUID
import play.api.http.HttpEntity
import akka.util.ByteString
import com.mohiva.play.silhouette.api.actions.SecuredRequest
import com.mohiva.play.silhouette.api.exceptions.ProviderException
import com.mohiva.play.silhouette.api.util.{ Credentials, PasswordInfo }
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider
import controllers.{ SilhouetteController, SilhouetteControllerComponents, routes }
import forms.{ ChangePasswordForm, SupportForm }

import javax.inject.Inject
import play.api.i18n.Messages
import play.api.libs.mailer.Email
import play.api.mvc.{ request, _ }
import utils.auth.{ DefaultEnv, WithProvider }
import views.html.emails.supportForm

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ Await, ExecutionContext, Future }

import scala.language.postfixOps

/**
 * The Download controller.
 */
class DownloadController @Inject() (
  scc: SilhouetteControllerComponents,
  downloadView: views.html.download,
  downloadThankView: views.html.downloadThank,
  home: views.html.home
)(implicit ex: ExecutionContext) extends SilhouetteController(scc) {

  /**
   * Views the Download page.
   *
   * @return The result to display.
   */
  def view: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)) {

    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
      //   if (downloadService.isJustDownloadedOrIsGettingDownloaded(request.identity.userID))
      //   Ok(downloadThankView(request.identity))
      //      else
      Ok(downloadView(request.identity))
  }

  def download: Action[AnyContent] = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)) {

    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>

      request.method.toLowerCase() match {

        case "get" =>

          Ok(downloadThankView(request.identity))

        case "post" => Await.result(downloadService.generateDownload(request.identity.loginInfo), 10 second) match {

          case Some(tempFile) =>

            val file = tempFile.path.toFile

            Ok.sendFile(

              file,

              fileName = _ => Some("YourCompanyEditor.txt"),

              onClose = () => {

                val downloadNotification = models.DownloadNotification(
                  id = UUID.randomUUID(),
                  userID = request.identity.userID,
                  organization = request.identity.organization,
                  email = request.identity.email,
                  firstName = request.identity.firstName,
                  lastName = request.identity.lastName,
                  roleTitle = request.identity.roleTitle)

                mailerClient.send(Email(
                  subject = Messages("email.download.subject"),
                  from = Messages("email.from"),
                  to = Seq(request.identity.email.get),
                  bodyText = Some(views.txt.emails.downloadForm(downloadNotification).body),
                  bodyHtml = Some(views.html.emails.downloadForm(downloadNotification).body)
                ))

                logger.debug("download notification sent to client")

                file.delete()

                downloadService.setDownloaded(request.identity.loginInfo)
                Redirect(routes.InfoController.view(Messages("download.thank.you"), Messages("download.title"))).flashing(s"Dear ${request.identity.firstName}" -> Messages("download.thank.you"))
                Ok(downloadThankView(request.identity))

              }
            ).withHeaders(
                "HttpResponse.entity.contentType" -> "text/txt",
                "Content-Disposition" -> s"attachment; filename=YourCompanyEditor.txt"
              )
              .flashing("success" -> (s"Dear ${request.identity.firstName} " + Messages("download.thank.you")))

          // Redirect(routes.DownloadController.thankView())

          case None =>

            Redirect(routes.InfoController.view(Messages("download.over.maximum.trial"), Messages("download.title")))

        }

      }

  }

  //  def download = SecuredAction(WithProvider[AuthType](CredentialsProvider.ID)).async {
  //    implicit request: SecuredRequest[DefaultEnv, AnyContent] =>
  //      downloadService.generateDownload(request.identity.userID).map({
  //        case Some(value) =>
  //          Ok.sendFile(
  //            value,
  //            fileName =  _ => Some(value.getName)
  //          ).withHeaders(
  //            "Content-Type" -> "text/txt",
  //            "Content-Disposition" -> s"attachment; filename=${value.getName}"
  //          )
  //
  //          val downloadNotification = models.DownloadNotification(
  //            id = UUID.randomUUID(),
  //            userID = request.identity.userID,
  //            organization = request.identity.organization,
  //            email = request.identity.email,
  //            firstName = request.identity.firstName,
  //            lastName = request.identity.lastName,
  //            roleTitle = request.identity.roleTitle)
  //
  //          mailerClient.send(Email(
  //            subject = Messages("email.download.subject"),
  //            from = Messages("email.from"),
  //            to = Seq(request.identity.email.get),
  //            bodyText = Some(views.txt.emails.downloadForm(downloadNotification).body),
  //            bodyHtml = Some(views.html.emails.downloadForm(downloadNotification).body)
  //          ))
  //
  //          logger.debug("download notification sent to client")
  //
  //          mailerClient.send(Email(
  //            subject = Messages("email.download.subject"),
  //            from = Messages("email.from"),
  //            to = Seq(Messages("email.from")),
  //            bodyText = Some(views.txt.emails.supportDownloadForm(downloadNotification).body),
  //            bodyHtml = Some(views.html.emails.supportDownloadForm(downloadNotification).body)
  //          ))
  //
  //          Redirect(routes.DownloadController.view()).flashing("info" -> Messages("download.notification.received"))
  //
  //        case None =>
  //          Redirect(routes.DownloadController.view()).flashing("info" -> Messages("download.over.maximum.trial"))
  //      })
  //
  //  }

}
