name := "fah"

version := "0.1"

scalaVersion := "2.12.10"

libraryDependencies ++= Seq(
  "ch.qos.logback"  %  "logback-classic"    % "1.2.3",
  "com.google.guava" % "guava" % "23.0",
  "org.json4s" %% "json4s-native" % "3.6.7",

  "org.apache.spark" %% "spark-core" % "2.4.4",
  "org.apache.spark" %% "spark-sql" % "2.4.4",
  "org.postgresql" % "postgresql" % "42.2.1",

  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
)

assemblyMergeStrategy in assembly := {
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}
