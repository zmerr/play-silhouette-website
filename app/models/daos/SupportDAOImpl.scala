package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.{ ContactTicket, SupportTicket }
import play.api.db.Database

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

/**
 * Give access to the user object.
 */
class SupportDAOImpl @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext) extends SupportDAO {

  //  implicit val dbBlockingExecutionConext: ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-DB-dispatcher"))

  def find(email: String): Future[Option[SupportTicket]] = ???

  def getALL(email: String): Future[Option[Seq[SupportTicket]]] = ???

  /**
   * Saves a user.
   *
   * @param supportTicket The supportTicket to save.
   * @return The saved supportTicket.
   */
  def save(supportTicket: SupportTicket): Future[SupportTicket] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        "INSERT INTO support_tickets ( " +
          "id, " +
          "userid, " +
          "subject, " +
          "roleTitle, " +
          "firstName, " +
          "lastName, " +
          "organization, " +
          "email, " +
          "message) " +
          s"VALUES ('${supportTicket.id}', '${supportTicket.userID}', ?, ?, ?, ?, ?, ?, ?);")

      statement.setString(1, supportTicket.subject.get)
      statement.setString(2, supportTicket.roleTitle.get)
      statement.setString(3, supportTicket.firstName.get)
      statement.setString(4, supportTicket.lastName.get)
      statement.setString(5, supportTicket.organization.get)
      statement.setString(6, supportTicket.email.get)
      statement.setString(7, supportTicket.message.get)

      statement.execute()
      statement.close()
      c.close()
      //   result = user

      supportTicket
    }

}
/**
 * The companion object.
 */
object SupportDAOImpl {

}

