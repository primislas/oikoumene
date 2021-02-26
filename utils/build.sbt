name := "utils"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.3.3" withSources() withJavadoc()
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.11.2" withSources() withJavadoc()
libraryDependencies += "org.apache.commons" % "commons-math3" % "3.6"
scalacOptions in Test ++= Seq("-Yrangepos")
