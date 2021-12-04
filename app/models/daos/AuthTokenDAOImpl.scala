package models.daos

import java.sql.Timestamp
import java.util.UUID

import javax.inject.Inject
import models.AuthToken
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.db.Database

import scala.concurrent.{ ExecutionContext, Future }

/**
 * Give access to the [[AuthToken]] object.
 */
class AuthTokenDAOImpl @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext) extends AuthTokenDAO {

  // implicit val dbBlockingExecutionConext: ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-DB-dispatcher"))

  /**
   * Finds a token by its ID.
   *
   * @param id The unique token ID.
   * @return The found token or None if no token for the given ID could be found.
   */
  def find(id: UUID): Future[Option[AuthToken]] =
    //Future.successful(tokens.get(id))
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(s"SELECT * from auth_tokens where id = ? ;")
      statement.setObject(1, id, java.sql.Types.OTHER)
      if (statement.execute()) {
        val resultSet = statement.getResultSet
        resultSet.next()
        val userID = UUID.fromString(resultSet.getString("userID"))

        val psqlTimeStamp = resultSet.getString("expiry")
        val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")
        val expiry = DateTime.parse(psqlTimeStamp, formatter)
        statement.close()
        c.close()
        Some(AuthToken(id, userID, expiry))

      } else {
        statement.close()
        c.close()
        None
      }
    }

  /**
   * Finds expired tokens.
   *
   * @param dateTime The current date time.
   */
  def findExpired(dateTime: DateTime): Future[Seq[AuthToken]] =
    //    Future.successful {
    //    tokens.filter {
    //      case (_, token) =>
    //        token.expiry.isBefore(dateTime)
    //    }.values.toSeq
    //  }
    Future {
      val dates = Seq[AuthToken]()
      val c = db.getConnection()
      val statement = c.prepareStatement("SELECT * from auth_tokens where expiry < (?);")
      statement.setTimestamp(1, new Timestamp(dateTime.getMillis))
      if (statement.execute()) {
        val resultSet = statement.getResultSet

        while (resultSet.next()) {

          val id = UUID.fromString(resultSet.getString("id"))
          val userID = UUID.fromString(resultSet.getString("userID"))

          val psqlTimeStamp = resultSet.getString("expiry")
          val formatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")
          val expiry = DateTime.parse(psqlTimeStamp, formatter)

          dates :+ AuthToken(id, userID, expiry)

        }

      }
      statement.close()
      c.close()
      dates
    }

  /**
   * Saves a token.
   *
   * @param token The token to save.
   * @return The saved token.
   */
  def save(token: AuthToken): Future[AuthToken] =
    //  {
    //    tokens += (token.id -> token)
    //    Future.successful(token)
    //  }
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        s"INSERT into auth_tokens (id, userID, expiry) VALUES ( ? , ? , ? );")
      statement.setObject(1, token.id, java.sql.Types.OTHER)
      statement.setObject(2, token.userID, java.sql.Types.OTHER)
      statement.setTimestamp(3, new Timestamp(token.expiry.getMillis))
      statement.execute()
      statement.close()
      c.close()
      token
    }
  /**
   * Removes the token for the given ID.
   *
   * @param id The ID for which the token should be removed.
   * @return A future to wait for the process to be completed.
   */
  def remove(id: UUID): Future[Unit] =
    //  {
    //    tokens -= id
    //    Future.successful(())
    //  }
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        s"DELETE FROM auth_tokens WHERE id = ?;")
      statement.setObject(1, id, java.sql.Types.OTHER)
      statement.execute()
      statement.close()
      c.close()
    }
}

/**
 * The companion object.
 */
object AuthTokenDAOImpl {

}
