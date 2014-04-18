import sbt._
import Keys._
import com.typesafe.sbt.SbtStartScript

object BuildSettings {
  import Dependencies._
  import Resolvers._

  val buildOrganization = "net.matthaynes"
  val buildVersion = "1.0"
  val buildScalaVersion = "2.9.2"

  val globalSettings = Seq(
        organization := buildOrganization,
        version := buildVersion,
        scalaVersion := buildScalaVersion,
        scalacOptions += "-deprecation",
        fork in test := true,
        libraryDependencies ++= Seq(scalaTest, mockito),
        resolvers := Seq(sonatypeRepo, snacktoryRepo)
      )

  val projectSettings = Defaults.defaultSettings ++ globalSettings
}

object Resolvers {
  val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
  // val snacktoryRepo = "karussell_releases" at "https://github.com/karussell/mvnrepo/raw/master/releases"
  val snacktoryRepo = "skyshard_snapshots" at "https://github.com/skyshard/mvnrepo/raw/master/snapshots"

}

object Dependencies {
  val scalaTest    = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  val scalatraTest = "org.scalatra" % "scalatra-scalatest_2.9.1" % "2.0.4" % "test"
  val scalatra     = "org.scalatra" % "scalatra_2.9.1" % "2.0.4"
  val liftJson     = "net.liftweb" % "lift-json_2.9.1" % "2.4"
  val mockito      = "org.mockito" % "mockito-core" % "1.8.4" % "test"

  val jettyVersion = "9.0.5.v20130815"
  val jettyServer = "org.eclipse.jetty" % "jetty-server" % jettyVersion
  val jettyServlet = "org.eclipse.jetty" % "jetty-servlet" % jettyVersion
  val jettyServerTest = jettyServer % "test"
  
  val slf4j = "org.slf4j" % "slf4j-log4j12" % "1.6.6"
  val slf4jTest = slf4j % "test"

  val corenlp = "edu.stanford.nlp" % "stanford-corenlp" % "1.3.4" classifier "models" classifier ""

  val commonsLang = "commons-lang" % "commons-lang" % "2.6" 

  val snacktory = "de.jetwick" % "snacktory" % "1.2.1-SNAPSHOT"

  val languageDetection = "com.cybozu.labs" % "langdetect" % "1.1-20120112"
}

object JuicerBuild extends Build {
    import BuildSettings._
    import Dependencies._
    import Resolvers._

    override lazy val settings = super.settings ++ globalSettings

    //lazy val goose = ProjectRef(uri("git://github.com/skyshard/goose.git"), "goose")
    lazy val goose = ProjectRef(file("../goose/"), "goose")

    lazy val root = Project("juicer",
                      file("."),
                      settings = projectSettings ++
                      Seq(
                          SbtStartScript.stage in Compile := Unit
                      )) aggregate(service, web)

    lazy val service = Project("juicer-service",
                      file("juicer-service"),
                      settings = projectSettings ++
                      Seq(libraryDependencies ++= Seq(slf4j, slf4jTest, corenlp, liftJson, commonsLang, snacktory, languageDetection))) dependsOn(goose)

    lazy val web = Project("juicer-web",
                      file("juicer-web"),
                      settings = projectSettings ++
                      SbtStartScript.startScriptForClassesSettings ++
                      Seq(libraryDependencies ++= Seq(jettyServer, jettyServlet, slf4j, liftJson, scalatra, scalatraTest))) dependsOn(service % "compile->compile;test->test")

}
