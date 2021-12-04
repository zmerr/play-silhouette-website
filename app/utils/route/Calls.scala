package utils.route

import play.api.mvc.Call

/**
 * Defines some common redirect calls used in authentication flow.
 */
object Calls {
  /** @return The URL to redirect to when an authentication succeeds. */
  def home: Call = controllers.routes.ApplicationController.index

  /** @return The URL to redirect to when an authentication fails. */
  def signin: Call = controllers.routes.SignInController.view

  /** @return The URL to redirect to when the contact form is successfully received. */
  def contact: Call = controllers.routes.ContactController.view

  def support: Call = controllers.routes.SupportController.view

  def download: Call = controllers.routes.DownloadController.view
}
