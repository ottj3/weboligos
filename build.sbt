name := """web-oligos"""

version := "1.1-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala, PlayEbean)

scalaVersion := "2.11.7"

unmanagedBase <<= baseDirectory { base => base / "run" }

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.adrianhurt" %% "play-bootstrap" % "1.1-P25-B3",
  "com.mohiva" %% "play-html-compressor" % "0.6.3"
)

fork in run := true