package forms

import play.api.data.Form
import play.api.data.Forms._

/**
 * The form which handles the sign up process.
 */
object SignUpForm {

  /**
   * A play framework form.
   */
  val form: Form[Data] = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "organization" -> nonEmptyText,
      "roleTitle" -> nonEmptyText,
      "email" -> email,
      "password" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param firstName The first name of a user.
   * @param lastName The last name of a user.
   * @param organization The organization of the user
   * @param roleTitle The roleTitle of the user
   * @param email The email of the user.
   * @param password The password of the user.
   */
  case class Data(
    firstName: String,
    lastName: String,
    organization: String,
    roleTitle: String,
    email: String,
    password: String)
}
