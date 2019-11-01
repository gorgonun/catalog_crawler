name := "catalog_crawler"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.12.1",
  "com.typesafe.akka" %% "akka-actor" % "2.5.26",
  "com.sun.mail" % "jakarta.mail" % "1.6.4"
)
enablePlugins(JavaAppPackaging)

herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value)