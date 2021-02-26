import sbt.Keys.libraryDependencies

lazy val commonSettings = Seq(
  organization := "com.lomicron",
  version := "0.4.0-SNAPSHOT",
  scalaVersion := "2.13.5",
  logLevel := Level.Info,
  libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0",
  libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.2" withSources() withJavadoc(),
  libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3",
  libraryDependencies += "org.specs2" %% "specs2-core" % "4.10.3" % "test" withSources() withJavadoc(),
  libraryDependencies += "org.specs2" %% "specs2-mock" % "4.10.3" % "test" withSources() withJavadoc()
)

lazy val utils = (project in file("utils"))
  .settings(commonSettings)
  
lazy val clausewitz = (project in file("clausewitz"))
  .dependsOn(utils)
  .aggregate(utils)
  .settings(commonSettings)

lazy val clausewitzRest = (project in file("clausewitz-rest"))
  .enablePlugins(PlayScala)
  .dependsOn(clausewitz)
  .aggregate(clausewitz)
  .settings(commonSettings)

lazy val oikoumene = (project in file("."))
  .settings(commonSettings)
  .enablePlugins(PlayScala)
  .dependsOn(clausewitzRest)
  .aggregate(clausewitzRest)
