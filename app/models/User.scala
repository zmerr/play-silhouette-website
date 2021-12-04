package models

import java.util.UUID

import com.mohiva.play.silhouette.api.{ Identity, LoginInfo }

/**
 * The user object.
 *
 * @param userID The unique ID of the user.
 * @param loginInfo The linked login info.
 * @param firstName  the first name of the authenticated user.
 * @param lastName  the last name of the authenticated user.
 * @param fullName  the full name of the authenticated user.
 * @param email  the email of the authenticated provider.
 * @param avatarURL Maybe the avatar URL of the authenticated provider.
 * @param activated Indicates that the user has activated its registration.
 */
case class User(
  userID: UUID,
  loginInfo: LoginInfo,
  firstName: Option[String],
  lastName: Option[String],
  organization: Option[String],
  roleTitle: Option[String],
  fullName: Option[String],
  email: Option[String],
  avatarURL: Option[String],
  activated: Boolean) extends Identity {

  /**
   * Tries to construct a name.
   *
   * @return  a name.
   */
  def name: Option[String] = fullName

}
