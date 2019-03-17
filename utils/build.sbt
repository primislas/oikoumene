name := "utils"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.14" withSources() withJavadoc()
libraryDependencies += "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.8" withSources() withJavadoc()
scalacOptions in Test ++= Seq("-Yrangepos")