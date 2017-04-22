package actors

import java.io.File
import java.net.{URI, URL}
import akka.actor.{Actor, ActorLogging}
import com.sun.org.apache.xml.internal.utils.URI.MalformedURIException
import models.Credential
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.xfer.FileSystemFile
import org.apache.commons.io.FilenameUtils
import utils.{Config, Protocol}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.sys.process._
import scala.util.control.NonFatal
import scala.util.{Failure, Success, Try}

object DownloaderActor {
  case class JavaUrlDownload(url: URL)
  case class SftpDownload(url: URI)

}

class DownloaderActor extends Actor with ActorLogging with Config {
  import DownloaderActor._

  def receive: Receive = {
    case JavaUrlDownload(url) => javaUrlDownload(url)
    case SftpDownload(url) => sftpDownload(url)
  }

  override def postStop(): Unit = {
    log.info("Waiting for download to complete")
    context.stop(self)
    context.system.terminate()
  }

  /*
    Protocol handlers for the following protocols are guaranteed to exist on the search path :-
    http, https, ftp, file, and jar
  */
  def javaUrlDownload(url: URL): Future[Unit] = {
    log.info(s"Started: $url")
    val finalUrl = getUrlWithCredential(url)
    val fileName = FilenameUtils.getName(url.getPath)
    val absoluteFileDirectory = s"$downloadDirectory/$fileName"
    val file = new File(absoluteFileDirectory)

    val downloadFuture = Try(finalUrl.openStream) match {
      case Success(_) =>
        Future {
          finalUrl #> file !!

          log.info(s"Downloaded: $url")
        }
      case Failure(ex) => Future.failed(ex)
    }

    downloadFuture recover {
      case NonFatal(ex) =>
        file.delete()
        log.error(s"File could not be downloaded: $ex")
    }
  }

  /*
    Protocol handlers for sftp
  */
  def sftpDownload(url: URI): Future[Unit] = {
    log.info(s"Started: $url")
    val fileName = FilenameUtils.getName(url.getPath)
    val absoluteFileDirectory = s"$downloadDirectory/$fileName"
    val file = new FileSystemFile(absoluteFileDirectory)
    val ssh = new SSHClient
    ssh.addHostKeyVerifier(new PromiscuousVerifier())

    val downloadFuture = Try(ssh.connect(url.getHost)) match {
      case Success(_) =>
        val auth = getCredential(url.getHost).getOrElse(Credential("", ""))
        ssh.authPassword(auth.username, auth.password)
        url.getScheme match {
          case Protocol.Sftp =>
            Future {
              try {
                val sftp = ssh.newSFTPClient
                try {
                  sftp.get(fileName, file)
                  log.info(s"Downloaded: $url")
                } finally {
                  sftp.close()
                }
              } finally {
                ssh.disconnect()
              }
            }
          case _ => Future.failed(new MalformedURIException("Invalid URL was given"))
        }
      case Failure(ex) => Future.failed(ex)
    }

    downloadFuture recover {
      case NonFatal(_) =>
        file.getFile.delete()
        log.error(s"File could not be downloaded: $url")
    }
  }

  private def getUrlWithCredential(url: URL): URL = url.getProtocol match {
    case protocol if Protocol.JavaAuthProtocols.contains(protocol) =>
      getCredential(url.getHost) match {
        case Some(Credential(username, password)) =>
          val newUrl = s"""${url.getProtocol}://$username:$password@${url.getHost}${url.getFile}"""
          new URL(newUrl)
        case _ => url
      }
    case _ => url
  }
}