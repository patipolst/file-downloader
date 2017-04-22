package utils

import java.io.File
import com.typesafe.config.ConfigFactory
import models.Credential
import scala.util.Try

/*
  Provides information from configuration file
*/
trait Config {
  private val config = ConfigFactory.load()
  private val downloadConfig = config.getConfig("download")

  val currentDirectory: String = new java.io.File(".").getCanonicalPath
  val destinationDirectory: String = downloadConfig.getString("directory")

  val downloadDirectory: String = currentDirectory + destinationDirectory
  Try(new File(downloadDirectory).mkdirs())

  def getCredential(host: String): Option[Credential] = {
    if (config.hasPath(host)) {
      val hostConfig = config.getConfig(host)
      val username = hostConfig.getString("username")
      val password = hostConfig.getString("password")
      Option(Credential(username, password))
    } else {
      None
    }
  }
}