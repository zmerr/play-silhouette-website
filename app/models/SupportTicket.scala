
package models

import java.util.UUID

case class SupportTicket(
  id: UUID,
  userID: UUID,
  firstName: Option[String],
  lastName: Option[String],
  organization: Option[String],
  roleTitle: Option[String],
  subject: Option[String],
  email: Option[String],
  message: Option[String]
)