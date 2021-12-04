package models.daos

import java.sql.Timestamp
import java.util.UUID

import com.mohiva.play.silhouette.api.{ AuthInfo, LoginInfo }
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import javax.inject.Inject
import models.AuthToken
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.db.Database

import scala.concurrent.Future
import scala.reflect.{ ClassTag, classTag }
import scala.util.Success

class PersistedAuthInfoDAO @Inject() (db: Database)(implicit executionContext: DatabaseExecutionContext, implicit val classTag: ClassTag[PasswordInfo]) extends DelegableAuthInfoDAO[PasswordInfo] {

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    val c = db.getConnection()
    val statement = c.prepareStatement(s"SELECT * from auth_infos where providerID = ? and providerKey = ? ;")
    statement.setString(1, loginInfo.providerID)
    statement.setString(2, loginInfo.providerKey)
    if (statement.execute()) {
      val resultSet = statement.getResultSet
      if (resultSet.next()) {
        val hasher = resultSet.getString("hasher")
        val password = resultSet.getString("password")
        val salt = resultSet.getString("salt")
        statement.close()
        c.close()
        Some(PasswordInfo(hasher = hasher, password = password, salt = Some(salt)))
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

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        "INSERT INTO auth_infos ( " +
          "providerID, " +
          "providerKey, " +
          "hasher, " +
          "password, " +
          "salt) " +
          s"VALUES ( ?, ?, ?, ?, ?);")
      statement.setString(1, loginInfo.providerID)
      statement.setString(2, loginInfo.providerKey)
      statement.setString(3, authInfo.hasher)
      statement.setString(4, authInfo.password)
      statement.setString(5, authInfo.salt.getOrElse(""))
      statement.execute()
      statement.close()
      c.close()
      authInfo
    }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Future {
      val f = find(loginInfo)
      f.onComplete {
        case Success(Some(passwordInfo)) =>
          val c = db.getConnection()
          val statement = c.prepareStatement(
            "UPDATE auth_infos SET ( " +
              "hasher, " +
              "password, " +
              "salt) = " +
              s" ( ?, ?, ?) WHERE providerID = ? and providerKey = ? ;")
          statement.setString(1, authInfo.hasher)
          statement.setString(2, authInfo.password)
          statement.setString(3, authInfo.salt.getOrElse(""))
          statement.setString(4, loginInfo.providerID)
          statement.setString(5, loginInfo.providerKey)
          statement.execute()
          statement.close()
          c.close()
        //  result = user
        case Success(None) =>
          val c = db.getConnection()
          val statement = c.prepareStatement(
            "INSERT INTO auth_infos ( " +
              "providerID, " +
              "providerKey, " +
              "hasher, " +
              "password, " +
              "salt) " +
              s"VALUES ( ?, ?, ?, ?, ?);")
          statement.setString(1, loginInfo.providerID)
          statement.setString(2, loginInfo.providerKey)
          statement.setString(3, authInfo.hasher)
          statement.setString(4, authInfo.password)
          statement.setString(5, authInfo.salt.getOrElse(""))
          statement.execute()
          statement.close()
          c.close()
        //   result = user
        case _ =>

      }
      authInfo
    }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] =
    Future {
      val c = db.getConnection()
      val statement = c.prepareStatement(
        "INSERT INTO auth_infos ( " +
          "providerID, " +
          "providerKey, " +
          "hasher, " +
          "password, " +
          "salt) " +
          s"VALUES ( ?, ?, ?, ?, ?);")
      statement.setString(1, loginInfo.providerID)
      statement.setString(2, loginInfo.providerKey)
      statement.setString(3, authInfo.hasher)
      statement.setString(4, authInfo.password)
      statement.setString(5, authInfo.salt.getOrElse(""))
      statement.execute()
      statement.close()
      c.close()
      authInfo
    }

  override def remove(loginInfo: LoginInfo): Future[Unit] = Future {
    val c = db.getConnection()
    val statement = c.prepareStatement(
      s"DELETE FROM auth_infos WHERE providerID = ? and providerKey = ? ;")
    statement.setString(1, loginInfo.providerID)
    statement.setString(2, loginInfo.providerKey)
    statement.execute()
    statement.close()
    c.close()
  }

}
/**
 * The companion object.
 */
object PersistedAuthInfoDAO {

}