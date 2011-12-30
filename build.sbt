name := "NER"

version := "0.1"

scalaVersion := "2.9.1"

scalacOptions += "-deprecation"

// Repos

resolvers += "Sonatype Nexus Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

resolvers += "Sonatype Nexus Releases" at "https://oss.sonatype.org/content/repositories/releases"

// Dependencies

libraryDependencies += "org.mockito" % "mockito-core" % "1.8.4" % "test"

libraryDependencies += "com.novocode" % "junit-interface" % "0.7" % "test->default"

libraryDependencies += "org.scalatest" %% "scalatest" % "1.6.1" % "test"