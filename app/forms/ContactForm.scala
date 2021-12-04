package forms

import play.api.data.Form
import play.api.data.Forms.{ email, mapping, nonEmptyText }

/**
 * The form which handles the sign up process.
 */
object ContactForm {

  /**
   * A play framework form.
   */
  val form: Form[Data] = Form(
    mapping(
      "firstName" -> nonEmptyText,
      "lastName" -> nonEmptyText,
      "organization" -> nonEmptyText,
      "roleTitle" -> nonEmptyText,
      "subject" -> nonEmptyText,
      "email" -> email,
      "message" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param firstName   The first name of a user.
   * @param lastName    The last name of a user.
   * @param organization The organization of the user
   * @param roleTitle   The job title of the user
   * @param subject     The subject of the message
   * @param email       The email of the user.
   * @param message     The message of the user.
   */
  case class Data(
    firstName: String,
    lastName: String,
    organization: String,
    roleTitle: String,
    subject: String,
    email: String,
    message: String)

}
