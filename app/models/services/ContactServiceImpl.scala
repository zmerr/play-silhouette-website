package models.services

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.{ ContactTicket, User }
import models.daos.{ ContactDAO, UserDAO }

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Handles actions to users.
 *
 * @param contactDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class ContactServiceImpl @Inject() (contactDAO: ContactDAO)(implicit ex: ExecutionContext) extends ContactService {

  /**
   * Retrieves a user that matches the specified ID.
   *
   * @param email The ID to retrieve a user.
   * @return The retrieved user or None if no user could be retrieved for the given ID.
   */
  def retrieve(email: String): Future[Option[ContactTicket]] = contactDAO.find(email)

  /**
   * Retrieves a user that matches the specified login info.
   *
   * @param email The email info to retrieve a user.
   * @return The retrieved contactTickets or None if no contactTicket could be retrieved for the given email.
   */
  def retrieveALL(email: String): Future[Option[Seq[ContactTicket]]] = contactDAO.getALL(email)

  /**
   * Saves a user.
   *
   * @param contactTicket The contactTicket to save.
   * @return The saved contactTicket.
   */
  def save(contactTicket: ContactTicket): Future[ContactTicket] = contactDAO.save(contactTicket)

}

object ContactServiceImpl {

}
