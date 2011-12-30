// // import web settings
seq(webSettings :_*)

name := "NER"

version := "0.1"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

// Repos

resolvers += "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

// Dependencies

libraryDependencies += "org.scalatra" %% "scalatra-scalatest" % "2.0.1" % "test"

libraryDependencies += "org.scalatra" %% "scalatra" % "2.0.1"

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

libraryDependencies += "org.mortbay.jetty" % "jsp-2.1-glassfish" % "2.1.v20100127" % "container"

libraryDependencies += "org.mockito" % "mockito-core" % "1.8.4" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.7" % "test->default"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.4-M5"

libraryDependencies += "javax.servlet" % "servlet-api" % "2.5" % "provided->default"

libraryDependencies += "javax" % "javaee-api" % "6.0" % "provided->default"

// Jetty config

port in container.Configuration := 8888