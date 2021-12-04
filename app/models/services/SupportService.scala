
package models.services

import java.util.UUID

import models.{ ContactTicket, SupportTicket, User }

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait SupportService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param email The email to retrieve a ContactTicket.
   * @return The retrieved ContactTicket or None if no user could be retrieved for the given ID.
   */
  def retrieve(email: String): Future[Option[SupportTicket]]

  /**
   * Saves a user.
   *
   * @param supportTicket The contactInfo to save.
   * @return The saved user.
   */
  def save(supportTicket: SupportTicket): Future[SupportTicket]

}

