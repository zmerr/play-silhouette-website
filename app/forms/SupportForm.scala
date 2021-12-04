package forms

import play.api.data.Form
import play.api.data.Forms.{ email, mapping, nonEmptyText }

/**
 * The form which handles the sign up process.
 */
object SupportForm {

  /**
   * A play framework form.
   */
  val form: Form[Data] = Form(
    mapping(
      "subject" -> nonEmptyText,
      "message" -> nonEmptyText
    )(Data.apply)(Data.unapply)
  )

  /**
   * The form data.
   *
   * @param subject     The subject of the message
   * @param message     The message of the user.
   */
  case class Data(
    subject: String,
    message: String)

}

