name := "catalog_crawler"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "org.jsoup" % "jsoup" % "1.12.1",
  "com.typesafe.akka" %% "akka-actor" % "2.5.26",

  "ch.qos.logback"  %  "logback-classic"    % "1.2.3",

  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.postgresql" % "postgresql" % "42.2.1",

  "org.scalaj" %% "scalaj-http" % "2.4.2",
  "org.json4s" %% "json4s-native" % "3.6.7",

  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",

  "com.google.guava" % "guava" % "23.0"
)

enablePlugins(JavaAppPackaging)

herokuFatJar in Compile := Some((assemblyOutputPath in assembly).value)


assemblyMergeStrategy in assembly := {
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
