
import java.io.File
import akka.actor.ActorSystem
import akka.testkit.TestKit
import org.apache.commons.io.FilenameUtils
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import utils.Config
import scala.util.Try

class MainSpec extends TestKit(ActorSystem("MainSpec")) with WordSpecLike
  with Matchers with BeforeAndAfterAll with Config {

  override def afterAll {
    TestKit.shutdownActorSystem(system)
    new File(downloadDirectory).listFiles().foreach(_.deleteOnExit())
  }

  "A Main" must {

    "download files in multiple protocols" in {
      val urls = Array(
        "http://mirror.unl.edu/ctan/macros/latex/contrib/famt/ProjectReport/README.txt",
        "https://ibm.box.com/shared/static/f1dhhjnzjwxmy2c1ys2whvrgz05d1pui.csv",
        "ftp://test.rebex.net/pub/example/ConsoleClient.png",
        s"file:///$currentDirectory/README.md",
        "jar:http://www.bioinformatics.org/jannotatix/jannotatix.jar!/img/Hammer.gif",
        "sftp://test.rebex.net/readme.txt"
      )

      val downloadFileNames = urls.map(url => FilenameUtils.getName(url))
      val absoluteFileDirectories = downloadFileNames.map(fileName => s"$downloadDirectory/$fileName")
      val downloadFiles = absoluteFileDirectories.map(new File(_))

      Main.main(urls)

      downloadFiles.foreach { file =>
        expectNoMsg
        file.exists() should be(true)
      }
    }

    "throw error when urls were not given" in {
      val urls = Array.empty[String]

      val result = Try(Main.main(urls))

      result.isFailure should be(true)
    }
  }
}
