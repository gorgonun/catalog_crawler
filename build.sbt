name := "fah"

version := "0.1"

lazy val doobieVersion = "0.8.8"
lazy val catsVersion = "2.1.1"

scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "ch.qos.logback"  %  "logback-classic"    % "1.2.3",
  "com.google.guava" % "guava" % "23.0",
  "org.json4s" %% "json4s-native" % "3.6.7",

  // FIXME: change scalike to doobie
  "org.scalikejdbc" %% "scalikejdbc"       % "3.4.1",
  "com.h2database"  %  "h2"                % "1.4.200",
  "ch.qos.logback"  %  "logback-classic"   % "1.2.3",

  "org.postgresql" % "postgresql" % "42.2.1",
  "org.apache.commons" % "commons-io" % "1.3.2",

  "org.scalactic" %% "scalactic" % "3.0.8",
  "org.scalatest" %% "scalatest" % "3.0.8" % "test",
)

assemblyMergeStrategy in assembly := {
  case "META-INF/services/org.apache.spark.sql.sources.DataSourceRegister" => MergeStrategy.concat
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

enablePlugins(JavaAppPackaging)

herokuAppName in Compile := "find-a-home"
