package actors

import java.io.File
import java.net.{URI, URL}
import actors.DownloaderActor._
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.TestKit
import org.apache.commons.io.FilenameUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import utils.Config

class DownloaderActorSpec extends TestKit(ActorSystem("DownloaderActorSpec")) with WordSpecLike
  with Matchers with BeforeAndAfterAll with Config {

  val downloaderActor: ActorRef = system.actorOf(Props[DownloaderActor])

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    new File(downloadDirectory).listFiles().foreach(_.delete())
  }

  "A Download actor" must {

    "download java url file (https)" in {
      val fileUrl = "https://www.ibm.com/support/knowledgecenter/en/SVU13_7.2.1/com.ibm.ismsaas.doc/reference/AssetsImportCompleteSample.csv"
      val url = new URL(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! JavaUrlDownload(url)

      expectNoMsg
      downloadFile.exists() should be(true)
    }

    "download java url file (ftp)" in {
      val fileUrl = "ftp://test.rebex.net/readme.txt"
      val url = new URL(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! JavaUrlDownload(url)

      expectNoMsg
      downloadFile.exists() should be(true)
    }

    "not download java url file with invalid url" in {
      val fileUrl = "http://mirror.unl.edu/ctan/macros/latex/contrib/famt/ProjectReport/READMEeeeee.txt"
      val url = new URL(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! JavaUrlDownload(url)

      expectNoMsg
      downloadFile.exists() should be(false)
    }

    "download file (sftp)" in {
      val fileUrl = "sftp://test.rebex.net/readme.txt"
      val url = new URI(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! SftpDownload(url)

      expectNoMsg
      downloadFile.exists() should be(true)
    }

    "not download file with unknown host (sftp)" in {
      val fileUrl = "sftp://hello.qqq.com/readme9999.txt"
      val url = new URI(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! SftpDownload(url)

      expectNoMsg
      downloadFile.exists() should be(false)
    }

    "not download file with invalid protocol (sftp)" in {
      val fileUrl = "ooo://aaaa.bbb.ccc/abc.txt"
      val url = new URI(fileUrl)
      val downloadFileName = FilenameUtils.getName(url.getPath)
      val absoluteFileDirectory = s"$downloadDirectory/$downloadFileName"
      val downloadFile = new File(absoluteFileDirectory)

      downloaderActor ! SftpDownload(url)

      expectNoMsg
      downloadFile.exists() should be(false)
    }
  }
}