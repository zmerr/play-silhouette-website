
package models

import java.util.UUID

case class DownloadNotification(
  id: UUID,
  userID: UUID,
  firstName: Option[String],
  lastName: Option[String],
  organization: Option[String],
  roleTitle: Option[String],
  email: Option[String]
)