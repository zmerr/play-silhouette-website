package models.services

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CredentialsProvider

import java.io.PrintWriter
import models.{ DownloadToken, InternalDownloadToken, User }
import models.daos.{ DownloadDAO, UserDAO }
import models.services.DownloadStatus.{ DownloadInitiated, DownloadStatus, Downloaded }
import play.api.libs.Files
import sun.security.krb5.Credentials

import java.util.concurrent.{ ConcurrentHashMap, ConcurrentMap }

//import Predef.{ intWrapper}
import java.util.UUID
import scala.util.Random
import java.security.SecureRandom
import javax.inject.Inject
import scala.concurrent.{ ExecutionContext, Future }
import scala.reflect.io.File

class DownloadServiceImpl @Inject() (downloadDAO: DownloadDAO, userDAO: UserDAO, configuration: play.api.Configuration)(implicit ex: ExecutionContext) extends DownloadService {

  import java.util.Base64

  val userDownloadStatusMap = new ConcurrentHashMap[String, DownloadStatus] //threadsafe

  val baseFile: File = File("base_file.txt")

  val maxTrial: Int = configuration.underlying.getInt("product.download.max.trial")

  def buildFile(downloadToken: DownloadToken): Files.TemporaryFile = {

    // Original File
    val tempFileName = "file" + s"${Random.nextInt()}.txt"

    var tempFile = Files.SingletonTemporaryFileCreator.create(tempFileName) // Temporary File

    // if (tempFile.exists()) tempFile.delete() //to make sure we start with an empty file

    //  tempFile = new File(tempFileName)

    val writer = new PrintWriter(tempFile)

    //    Source.fromFile(baseFile)
    //      .getLines
    //      //  .map { x => if(x.contains("proxy")) s"# $x" else x }
    //      .foreach(x => writer.println(x))

    writer.println(downloadToken)

    writer.close()

    tempFile
  }

  override def generateDownload(loginInfo: LoginInfo): Future[Option[Files.TemporaryFile]] =
    userDAO.find(loginInfo).map({
      case Some(user) =>
        downloadDAO.getALL(user.userID).map(

          tokenSeq =>

            if (tokenSeq.map(_ => 1).sum >= maxTrial) {

              Future.successful(None)
            } else {
              val number = tokenSeq.map(_.number).fold(0)(_ max _) + 1

              val downloadToken = DownloadToken("token", loginInfo.providerKey, number)

              userDownloadStatusMap.put(loginInfo.providerKey, DownloadStatus.DownloadInitiated)

              downloadDAO.save(InternalDownloadToken(downloadToken.token, user.userID, downloadToken.number))
                .map(_ => Some(buildFile(downloadToken)))

            }

        ).flatten
      case None => Future.successful(None)
    }).flatten

  def verifyToken(downloadToken: DownloadToken): Future[Option[User]] =
    userDAO.find(LoginInfo(CredentialsProvider.ID, downloadToken.email)).map({
      case Some(user) => downloadDAO.getALL(user.userID).map(_.find(token => token.token == downloadToken.token && token.number == downloadToken.number)
        .map(_ => user)
      )
      case None => Future.successful(None)
    }).flatten

  def isJustDownloadedOrIsGettingDownloaded(loginInfo: LoginInfo): Boolean =
    if (userDownloadStatusMap.contains(loginInfo.providerKey)) true else false

  def setDownloaded(loginInfo: LoginInfo): Unit =
    userDownloadStatusMap.put(loginInfo.providerKey, Downloaded)

  def remove(loginInfo: LoginInfo): Unit =
    userDownloadStatusMap.remove(loginInfo.providerKey)

}

object DownloadServiceImpl {

}

object DownloadStatus extends Enumeration {
  type DownloadStatus = Value
  val DownloadInitiated, Downloaded = Value
}

