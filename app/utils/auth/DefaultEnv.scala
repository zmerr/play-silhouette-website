package utils.auth

import com.mohiva.play.silhouette.api.Env
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import models.{ ContactTicket, SupportTicket, User }

/**
 * The default env.
 */
trait DefaultEnv extends Env {
  type I = User
  type A = CookieAuthenticator
  type C = ContactTicket
  type S = SupportTicket
}
