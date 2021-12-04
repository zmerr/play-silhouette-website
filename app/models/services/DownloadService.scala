package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import models.{ DownloadToken, User }
import play.api.libs.Files

import java.util.UUID
import scala.concurrent.Future
import scala.reflect.io.File

trait DownloadService {

  def generateDownload(loginInfo: LoginInfo): Future[Option[Files.TemporaryFile]]

  def verifyToken(downloadToken: DownloadToken): Future[Option[User]]

  def isJustDownloadedOrIsGettingDownloaded(loginInfo: LoginInfo): Boolean

  def setDownloaded(loginInfo: LoginInfo): Unit

  def remove(loginInfo: LoginInfo): Unit

}
