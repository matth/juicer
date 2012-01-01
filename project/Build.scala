import sbt._
import Keys._
import com.typesafe.startscript.StartScriptPlugin

object BuildSettings {
  import Dependencies._
  import Resolvers._

  val buildOrganization = "net.matthaynes"
  val buildVersion = "1.0"
  val buildScalaVersion = "2.9.0-1"

  val globalSettings = Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        scalacOptions += "-deprecation",
        fork in test := true,
        libraryDependencies ++= Seq(scalaTest, mockito),
        resolvers := Seq(sonatypeRepo, akkaRepo, guiceyfruit)
      )

  val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
  val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
  val akkaRepo     = "Akka Maven Repository" at "http://akka.io/repository"
  val guiceyfruit  = "GuiceyFruit Release Repository" at "http://guiceyfruit.googlecode.com/svn/repo/releases/"
}

object Dependencies {
  val scalaTest    = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % "2.0.1" % "test"
  val scalatra     = "org.scalatra" %% "scalatra" % "2.0.1"
  val liftJson     = "net.liftweb" %% "lift-json" % "2.4-M5"
  val mockito      = "org.mockito" % "mockito-core" % "1.8.4" % "test"

  val jettyVersion = "7.4.0.v20110414"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyServerTest = jettyServer % "test"

  val slf4jSimple = "org.slf4j" % "slf4j-simple" % "1.6.2"
  val slf4jSimpleTest = slf4jSimple % "test"

  // Goose deps, they have no maven repo, https://github.com/jiminoc/goose/issues/29
  val jsoup      = "org.jsoup" % "jsoup" % "1.6.1"
  val commonsIO  = "commons-io" % "commons-io" % "2.0.1"
  val httpComponents = "org.apache.httpcomponents" % "httpclient" % "4.1.2"
  val commonsLang    = "commons-lang" % "commons-lang" % "2.6"
  val akkaActor      = "se.scalablesolutions.akka" % "akka-actor" % "1.1.3"
  val akkaTypedActor = "se.scalablesolutions.akka" % "akka-typed-actor" % "1.1.3"
}

object JuicerBuild extends Build {
    import BuildSettings._
    import Dependencies._
    import Resolvers._

    override lazy val settings = super.settings ++ globalSettings

    lazy val root = Project("juicer",
                      file("."),
                      settings = projectSettings ++
                      Seq(
                          StartScriptPlugin.stage in Compile := Unit
                      )) aggregate(service, web)

    lazy val service = Project("juicer-service",
                      file("juicer-service"),
                      settings = projectSettings ++
                      Seq(libraryDependencies ++= Seq(slf4jSimple, slf4jSimpleTest, jsoup, commonsIO, httpComponents, commonsLang, akkaActor, akkaTypedActor)))


    lazy val web = Project("juicer-web",
                      file("juicer-web"),
                      settings = projectSettings ++
                      StartScriptPlugin.startScriptForClassesSettings ++
                      Seq(libraryDependencies ++= Seq(jettyServer, jettyServlet, slf4jSimple, liftJson, scalatra, scalatraTest))) dependsOn(service % "compile->compile;test->test")

}