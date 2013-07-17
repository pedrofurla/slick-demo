
name := "slick-demo"

scalaVersion := "2.10.2"

initialCommands in console :=
  """|import slickdemo.dal._
     |import DAL._
     |import dataLayer.profile.simple._
     |import slickdemo._
     |import slickdemo.Main._
     |import slickdemo.Macros._
     |import scala.slick.jdbc.{GetResult, StaticQuery => Q}
     |import scala.slick.jdbc.GetResult._
     |import Q.interpolation
     |//val cm= reflect.runtime.currentMirror
     |//val u = cm.universe
     |//createDb
     |main(Array.empty)
     |""".stripMargin

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
)

mainClass := Some("slickdemo.Main")

