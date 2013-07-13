package slickdemo

import dal._
import DAL._
import dataLayer.profile.simple._
import org.joda.time.DateTime

/**
 * Created with IntelliJ IDEA.
 * User: pedrofurla
 * Date: 10/07/13
 * Time: 04:31
 * To change this template use File | Settings | File Templates.
 */
object AdHocQueries extends App {

  import scala.slick.lifted._
  import scala.slick.ast.{Node, Library}
  import scala.slick.lifted.Column
  import scala.language.implicitConversions
  import scala.language.postfixOps

  Main.init

  class DateColumnExtensionMethods[B1, P1](val c: Column[P1]) extends AnyVal with ExtensionMethods[B1, P1] {
    def -[P2, R](e: Column[P2])(implicit om: o#arg[B1, P2]#to[B1, R]) =
      om(Library.-.column[B1](n, Node(e)))
  }

  implicit def dateColumnExtensionMethods2(c: Column[JDATE])(implicit tm: BaseTypeMapper[JDATE])
  = new DateColumnExtensionMethods[JDATE, JDATE](c)

  inSession {
    println(
      { for {p1 <- Persons.all if p1.id === 1
             p2 <- Persons.all if p2.id === 2} yield ((p1.birthday - p2.birthday).asColumnOf[Int])
      } first)}


  import Macros._

  debugExpr {
    def dayOfWeek(c: Column[JDATE]) = SimpleFunction[Int]("DAY_OF_WEEK").apply(Seq(c))
    println(inSession { (Persons.all map { p1 => (p1.fullName, dayOfWeek(p1.birthday)) }) list } mkString "\n")
  }
  Main.printSpacer

  debugExpr {
    def diffYear(a: Column[JDATE], b: Column[JDATE]) = SimpleFunction[Int]("DATEDIFF").apply(Seq(ConstColumn("MONTH"), a, b))
    println(inSession { (Persons.all map { p1 => (p1.fullName, diffYear(p1.birthday, DateTime.now) / 12) }) list } mkString "\n")
  }
  Main.printSpacer

  debugExpr {
    def diffYear(a: Column[JDATE], b: Column[JDATE]) = SimpleFunction[Int]("DATEDIFF").apply(Seq(ConstColumn("MONTH"), a, b))
    def ageNow(c: Column[JDATE]) = diffYear(c, DateTime.now) / 12
    println(inSession { (Persons.all map { p1 => (p1.fullName, ageNow(p1.birthday)) }).list } mkString "\n")
    println("Average Age: "+inSession { (Persons.all.map { p1 => (ageNow(p1.birthday)) }).avg.run })
  }

  println("Authors and how many works")
  debugExpr {
    println(inSession {
      (for{
        ba <- BookAuthors.all;
        a <- ba.authorFk } yield (ba,a)) groupBy {
          case (ba,a) => (a.id,a.name)
      } map { case ((id,name),y) => (name,id.count) } list

    })
  }
}
