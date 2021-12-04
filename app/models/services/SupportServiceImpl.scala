package models.services

import java.util.UUID

import javax.inject.Inject
import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import models.{ ContactTicket, SupportTicket, User }
import models.daos.{ ContactDAO, SupportDAO, UserDAO }

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Handles actions to users.
 *
 * @param supportDAO The user DAO implementation.
 * @param ex      The execution context.
 */
class SupportServiceImpl @Inject() (supportDAO: SupportDAO)(implicit ex: ExecutionContext) extends SupportService {

  def retrieve(email: String): Future[Option[SupportTicket]] = supportDAO.find(email)

  def retrieveALL(email: String): Future[Option[Seq[SupportTicket]]] = supportDAO.getALL(email)

  def save(supportTicket: SupportTicket): Future[SupportTicket] = supportDAO.save(supportTicket)

}

object SupportServiceImpl {

}

