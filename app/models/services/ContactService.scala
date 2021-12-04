
package models.services

import java.util.UUID

import models.{ ContactTicket, User }

import scala.concurrent.Future

/**
 * Handles actions to users.
 */
trait ContactService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param email The email to retrieve a ContactTicket.
   * @return The retrieved ContactTicket or None if no user could be retrieved for the given ID.
   */
  def retrieve(email: String): Future[Option[ContactTicket]]

  /**
   * Saves a user.
   *
   * @param contactTicket The contactInfo to save.
   * @return The saved user.
   */
  def save(contactTicket: ContactTicket): Future[ContactTicket]

}
