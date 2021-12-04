package models.daos

import java.util.UUID

import com.mohiva.play.silhouette.api.LoginInfo
import javax.inject.Inject
import models.User
import play.api.db.Database

import scala.concurrent.{ ExecutionContext, Future }
import scala.util.{ Failure, Success }

/**
 * Give access to the user object.
 */
class UserDAOImpl @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext) extends UserDAO {

  //  implicit val dbBlockingExecutionConext: ExecutionContext = system.dispatchers.lookup(DispatcherSelector.fromConfig("my-DB-dispatcher"))

  /**
   * Finds a user by its login info.
   *
   * @param loginInfo The login info of the user to find.
   * @return The found user or None if no user for the given login info could be found.
   */
  def find(loginInfo: LoginInfo): Future[Option[User]] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement("SELECT * FROM users WHERE providerID = ? AND providerKey = ?;")
      statement.setString(1, loginInfo.providerID)
      statement.setString(2, loginInfo.providerKey)
      if (statement.execute()) {
        val resultSet = statement.getResultSet
        if (resultSet.next()) {
          val userID = resultSet.getString("userid")
          val providerID = resultSet.getString("providerID")
          val providerKey = resultSet.getString("providerKey")
          val firstName = resultSet.getString("firstName")
          val lastName = resultSet.getString("lastName")
          val organization = resultSet.getString("organization")
          val roleTitle = resultSet.getString("roleTitle")
          val fullName = resultSet.getString("fullName")
          val email = resultSet.getString("email")
          val avatarURL = resultSet.getString("avatarURL")
          val activatedStr = resultSet.getString("activated")
          val activated: Boolean = activatedStr match {
            case "f" => false
            case "t" => true
            case _ => false
          }
          statement.close()
          c.close()
          Some(
            User(
              UUID.fromString(userID),
              LoginInfo(providerID, providerKey),
              firstName = Some(firstName),
              lastName = Some(lastName),
              organization = Some(organization),
              roleTitle = Some(roleTitle),
              fullName = Some(fullName),
              email = Some(email),
              avatarURL = Some(avatarURL),
              activated))

        } else {
          statement.close()
          c.close()
          None
        }
      } else {
        statement.close()
        c.close()
        None
      }
    }

  /**
   * Finds a user by its user ID.
   *
   * @param userID The ID of the user to find.
   * @return The found user or None if no user for the given ID could be found.
   */
  def find(userID: UUID): Future[Option[User]] = {
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(s"SELECT * FROM users WHERE userID = ? ;")
      statement.setObject(1, userID, java.sql.Types.OTHER)
      if (statement.execute()) {
        val resultSet = statement.getResultSet
        if (resultSet.next()) {
          val providerID = resultSet.getString("providerID")
          val providerKey = resultSet.getString("providerKey")
          val firstName = resultSet.getString("firstName")
          val lastName = resultSet.getString("lastName")
          val organization = resultSet.getString("organization")
          val roleTitle = resultSet.getString("roleTitle")
          val fullName = resultSet.getString("fullName")
          val email = resultSet.getString("email")
          val avatarURL = resultSet.getString("avatarURL")
          val activated = resultSet.getString("activated") match {
            case "f" => false
            case "t" => true
            case _ => false
          }
          statement.close()
          c.close()
          Some(
            User(
              userID,
              LoginInfo(providerID, providerKey),
              firstName = Some(firstName),
              lastName = Some(lastName),
              organization = Some(organization),
              roleTitle = Some(roleTitle),
              fullName = Some(fullName),
              email = Some(email),
              avatarURL = Some(avatarURL),
              activated))
        } else {
          statement.close()
          c.close()
          None
        }
      } else {
        statement.close()
        c.close()
        None
      }
    }
  }

  /**
   * Saves a user.
   *
   * @param user The user to save.
   * @return The saved user.
   */
  def save(user: User): Future[User] =
    Future {
      val f = find(user.userID)
      f.onComplete {
        case Success(Some(dbUser)) =>
          val c = db.getConnection()
          val statement = c.prepareStatement(
            "UPDATE users SET ( " +
              "providerID, " +
              "providerKey, " +
              "firstName, " +
              "lastName, " +
              "organization, " +
              "roleTitle, " +
              "email, " +
              "avatarURL, " +
              "activated) = " +
              s" ( ?, ?, ?, ?, ?, ?, ?, ?, ?) WHERE userid = ?;")
          statement.setString(1, user.loginInfo.providerID)
          statement.setString(2, user.loginInfo.providerKey)
          statement.setString(3, user.firstName.get)
          statement.setString(4, user.lastName.get)
          statement.setString(5, user.organization.get)
          statement.setString(6, user.roleTitle.get)
          statement.setString(7, user.email.get)
          statement.setString(8, user.avatarURL.getOrElse(""))
          statement.setBoolean(9, user.activated)
          statement.setObject(10, user.userID, java.sql.Types.OTHER)

          statement.execute()
          statement.close()
          c.close()
        //  result = user
        case Success(None) =>
          val c = db.getConnection()
          val statement = c.prepareStatement(
            "INSERT INTO users ( " +
              "userid, " +
              "providerID, " +
              "providerKey, " +
              "firstName, " +
              "lastName, " +
              "organization, " +
              "roleTitle, " +
              "email, " +
              "avatarURL, " +
              "activated) " +
              s"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
          statement.setObject(1, user.userID, java.sql.Types.OTHER)
          statement.setString(2, user.loginInfo.providerID)
          statement.setString(3, user.loginInfo.providerKey)
          statement.setString(4, user.firstName.get)
          statement.setString(5, user.lastName.get)
          statement.setString(6, user.organization.get)
          statement.setString(7, user.roleTitle.get)
          statement.setString(8, user.email.get)
          statement.setString(9, user.avatarURL.getOrElse(""))
          statement.setBoolean(10, user.activated)
          statement.execute()
          statement.close()
          c.close()
        //   result = user
        case _ =>

      }
      user
    }

}
/**
 * The companion object.
 */
object UserDAOImpl {

}
