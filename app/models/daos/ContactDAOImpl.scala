package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.ContactTicket
import play.api.db.Database

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

/**
 * Give access to the user object.
 */
class ContactDAOImpl @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext) extends ContactDAO {

  //  implicit val dbBlockingExecutionConext: ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-DB-dispatcher"))

  /**
   * Finds a user by its login info.
   *
   * @param email The email info of the contactTicket to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(email: String): Future[Option[ContactTicket]] = ???

  /**
   * Finds a user by its user ID.
   *
   * @param email The ID of the contactTickets to find.
   * @return The found contactTickets or None if no user for the given ID could be found.
   */
  def getALL(email: String): Future[Option[Seq[ContactTicket]]] = ???

  /**
   * Saves a user.
   *
   * @param contactTicket The contactTicket to save.
   * @return The saved contactTicket.
   */
  def save(contactTicket: ContactTicket): Future[ContactTicket] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        "INSERT INTO contact_tickets ( " +
          "id, " +
          "subject, " +
          "roleTitle, " +
          "firstName, " +
          "lastName, " +
          "organization, " +
          "email, " +
          "message) " +
          s"VALUES ('${contactTicket.id}', ?, ?, ?, ?, ?, ?, ?);")

      statement.setString(1, contactTicket.subject.get)
      statement.setString(2, contactTicket.roleTitle.get)
      statement.setString(3, contactTicket.firstName.get)
      statement.setString(4, contactTicket.lastName.get)
      statement.setString(5, contactTicket.organization.get)
      statement.setString(6, contactTicket.email.get)
      statement.setString(7, contactTicket.message.get)

      statement.execute()
      statement.close()
      c.close()
      //   result = user

      contactTicket
    }

}
/**
 * The companion object.
 */
object ContactDAOImpl {

}

