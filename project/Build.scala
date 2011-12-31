import sbt._
import Keys._
import com.github.siasia._
import WebPlugin._
import PluginKeys._

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
        libraryDependencies ++= Seq(scalaTest),
        resolvers := Seq(sonatypeRepo)
      )

  val projectSettings = Defaults.defaultSettings ++ globalSettings ++ webSettings
}

object Resolvers {
  val sonatypeRepo = "Sonatype Release" at "http://oss.sonatype.org/content/repositories/releases"
}

object Dependencies {
  val scalatraTest = "org.scalatra" %% "scalatra-scalatest" % "2.0.1" % "test"
  val scalatra     = "org.scalatra" %% "scalatra" % "2.0.1"
  val jetty        = "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"
  val jsp          = "org.mortbay.jetty" % "jsp-2.1-glassfish" % "2.1.v20100127" % "container"
  val scalaTest    = "org.scalatest" %% "scalatest" % "1.6.1" % "test"
  val liftJson     = "net.liftweb" %% "lift-json" % "2.4-M5"
  val servletApi   = "javax.servlet" % "servlet-api" % "2.5" % "provided->default"
  val javaeeApi    = "javax" % "javaee-api" % "6.0" % "provided->default"
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
                              port in container.Configuration := 8888,
                              libraryDependencies ++= Seq(scalatraTest, scalatra, jetty, jsp, liftJson, servletApi, javaeeApi)
                              )
                            )
}