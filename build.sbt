name := "FileDownloader"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies ++= {
  Seq(
    "com.typesafe.akka"          %% "akka-actor"            % "2.5.0",
    "com.typesafe.akka"          %% "akka-testkit"          % "2.5.0",
    "com.typesafe"               %  "config"                % "1.3.1",
    "com.typesafe.scala-logging" %% "scala-logging"         % "3.5.0",
    "ch.qos.logback"             %  "logback-classic"       % "1.2.3",
    "com.hierynomus"             %  "sshj"                  % "0.21.0",
    "commons-net"                %  "commons-net"           % "3.6",
    "commons-io"                 %  "commons-io"            % "2.5",
    "org.scalatest"              %% "scalatest"             % "3.0.1"           % Test
  )
}

parallelExecution in Test := false