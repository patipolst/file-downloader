package utils

/*
  Provides common protocol name as String
*/
object Protocol {
  val Http: String = "http"
  val Https: String = "https"
  val Ftp: String = "ftp"
  val Sftp: String = "sftp"

  val JavaAuthProtocols: List[String] = List(Http, Https, Ftp)
}