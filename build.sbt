lazy val commonSettings = Seq(
  organization := "com.lomicron",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  logLevel := Level.Info,
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0" withSources() withJavadoc(),
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3" withSources() withJavadoc(),
  libraryDependencies += "org.specs2" %% "specs2-core" % "3.9.1" % "test" withSources() withJavadoc(),
  libraryDependencies += "org.specs2" %% "specs2-mock" % "3.9.1" % "test" withSources() withJavadoc()
)

lazy val utils = (project in file("utils"))
  .settings(commonSettings)
  
lazy val clausewitz = (project in file("clausewitz"))
  .settings(commonSettings)
  .dependsOn(utils)

lazy val oikoumene = (project in file("."))
  .aggregate(utils, clausewitz)

  