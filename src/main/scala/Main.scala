
import java.net.{URI, URL}
import actors.DownloaderActor
import actors.DownloaderActor._
import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import com.typesafe.scalalogging.StrictLogging
import utils.Protocol
import scala.util.{Success, Try}

object Main extends StrictLogging {
  val system = ActorSystem("DownloadSystem")
  val downloaderActor: ActorRef = system.actorOf(Props[DownloaderActor], "downloaderActor")

  def main(args: Array[String]): Unit = {
    if (args.isEmpty) {
      throw new IllegalArgumentException("URLs must be given")
    } else {
      args.foreach(download)
    }
    downloaderActor ! PoisonPill
  }

  def download(url: String): Unit = Try(new URL(url)) match {
    case Success(javaUrl) => downloaderActor ! JavaUrlDownload(javaUrl)
    case _ => Try(new URI(url)) match {
      case Success(sftpUrl) if sftpUrl.getScheme == Protocol.Sftp => downloaderActor ! SftpDownload(sftpUrl)
      case _ => logger.error("Invalid URL was given")
    }
  }
}