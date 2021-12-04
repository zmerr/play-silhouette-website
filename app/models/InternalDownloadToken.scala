package models

import java.util.UUID

case class InternalDownloadToken(token: String, userID: UUID, number: Int)
