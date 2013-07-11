import sbt._
import Keys._

object ApplicationBuild extends Build {

  val appName = "slick-demo"
  val appVersion = "1.0-SNAPSHOT"
  val localScalaVersion = "2.10.2"

  val sharedSettings = Defaults.defaultSettings ++ Seq(
    scalaVersion := localScalaVersion,
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-Yrangepos"),
    resolvers ++= Seq(
    "Sonatype snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/",
      "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/",
      "Typesafe Snapshots" at "http://repo.typesafe.com/typesafe/snapshots/",
      Resolver.url(
        "Typesafe Ivy Snapshots",
        url("http://repo.typesafe.com/typesafe/ivy-snapshots/"))(Resolver.ivyStylePatterns),
      Resolver.url(
        "Typesafe Snapshots with ivy style",
        url("http://repo.typesafe.com/typesafe/snapshots/"))(Resolver.ivyStylePatterns)
    ),
    libraryDependencies ++= Seq(
      "org.specs2" %% "specs2" % "2.1-SNAPSHOT" % "test",
      "org.scala-lang" % "scala-reflect" % "2.10.2",
      "com.typesafe.slick" %% "slick" % "1.0.1",
      "org.slf4j" % "slf4j-nop" % "1.6.4",
      "joda-time" % "joda-time" % "2.2",
      "org.joda" % "joda-convert" % "1.3.1",
      // -- JDBC
      "com.h2database" % "h2" % "1.3.172",
      "mysql" % "mysql-connector-java" % "5.1.12",
      "org.xerial" % "sqlite-jdbc" % "3.7.2"
    )
  )

  lazy val macros = Project(
    id = "macros",
    base = file("macros"),
    settings = sharedSettings
  )
  lazy val root = Project(
    id = "slick-demo",
    base = file("."),
    settings = sharedSettings
  ) dependsOn (macros)
}

