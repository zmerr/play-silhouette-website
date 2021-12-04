package models.daos

import java.util.UUID

import models.{ ContactTicket, SupportTicket, User }

import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait SupportDAO {

  /**
   * Finds a user by its login info.
   *
   * @param email The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(email: String): Future[Option[SupportTicket]]

  /**
   * Finds a user by its user ID.
   *
   * @param email The email of the contactTicket to find.
   * @return The Seuence of found contactTickets or None if no contactTicket for the given email could be found.
   */
  def getALL(email: String): Future[Option[Seq[SupportTicket]]]

  /**
   * Saves a user.
   *
   * @param supportTicket The user to save.
   * @return The saved contactTicket.
   */
  def save(supportTicket: SupportTicket): Future[SupportTicket]
}
