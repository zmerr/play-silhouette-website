package models.daos

import models.{ DownloadToken, InternalDownloadToken, SupportTicket, User }

import java.util.UUID
import scala.concurrent.Future

/**
 * Give access to the user object.
 */
trait DownloadDAO {

  /**
   * Finds a user by its user ID.
   *
   * @return The Sequence of found download tokens or None if no download token for the given useruuid could be found.
   */
  def getALL(userUUID: UUID): Future[Seq[InternalDownloadToken]]

  /**
   * Saves a user.
   *
   * @param downloadToken The token to save.
   * @return The saved download token.
   */
  def save(downloadToken: InternalDownloadToken): Future[InternalDownloadToken]

  /**
   *
   * @param downloadToken the token to be verified against the records
   * @return the retrieved copy of the token to be verified or None if it doesn't exist.
   */
  def verify(downloadToken: InternalDownloadToken): Future[Option[InternalDownloadToken]]
}
