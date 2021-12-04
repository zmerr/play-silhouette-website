package models.daos

import models.InternalDownloadToken
import play.api.db.Database

import java.util.UUID
import javax.inject.Inject
import scala.concurrent.Future

/**
 * Give access to the user object.
 */
class DownloadDAOImpl @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext) extends DownloadDAO {

  //  implicit val dbBlockingExecutionConext: ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-DB-dispatcher"))

  /**
   * Saves a user.
   *
   * @param downloadToken The downloadToken to save.
   * @return The saved downloadToken.
   */
  def save(downloadToken: InternalDownloadToken): Future[InternalDownloadToken] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        "INSERT INTO download_tokens ( " +
          "number, " +
          "token, " +
          "userid) " +
          s"VALUES ( ? ,?, ?);")

      statement.setInt(1, downloadToken.number)
      statement.setString(2, downloadToken.token)
      statement.setObject(3, downloadToken.userID, java.sql.Types.OTHER)
      statement.execute()
      statement.close()
      c.close()
      //   result = user
      downloadToken
    }

  /**
   * Finds a user by its user ID.
   *
   * @return The Sequence of found download tokens or None if no download token for the given useruuid could be found.
   */
  def getALL(userUUID: UUID): Future[Seq[InternalDownloadToken]] =
    Future {
      val c = db.getConnection()
      var tokenSeq: Seq[InternalDownloadToken] = Seq.empty
      val statement = c.prepareStatement(s"SELECT * FROM download_tokens WHERE userid = ? ;")
      statement.setObject(1, userUUID, java.sql.Types.OTHER)
      if (statement.execute()) {
        val resultSet = statement.getResultSet
        while (resultSet.next()) {
          val token = resultSet.getString("token")
          val number = resultSet.getString("number").toInt
          tokenSeq = tokenSeq :+ InternalDownloadToken(userID = userUUID, token = token, number = number)
        }

      }
      statement.close()
      c.close()
      tokenSeq
    }

  /**
   *
   * @param downloadToken the token to be verified against the records
   * @return the retrieved copy of the token to be verified or None if it doesn't exist.
   */
  def verify(downloadToken: InternalDownloadToken): Future[Option[InternalDownloadToken]] =
    Future {
      val c = db.getConnection()
      var fetchedDownloadToken: Option[InternalDownloadToken] = None
      val statement = c.prepareStatement(s"SELECT * FROM download_tokens WHERE token = ? ;")
      statement.setString(1, downloadToken.token)
      val resultSet = statement.getResultSet
      if (statement.execute()) {

        if (resultSet.next()) {
          val token = resultSet.getString("token")
          val number = resultSet.getString("number").toInt
          val userID = UUID.fromString(resultSet.getString("userID"))

          fetchedDownloadToken = Some(InternalDownloadToken(token, userID, number))
        }

      }
      statement.close()
      c.close()
      fetchedDownloadToken

    }
}

/**
 * The companion object.
 */
object DownloadDAOImpl {

}

