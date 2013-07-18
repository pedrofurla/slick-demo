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


  import scala.language.implicitConversions
  import scala.language.postfixOps

  init

  printSpacer("Some lifted embedding Slick query examples")

  println

  /*printSpacer("Operators in customs data types")
  import scala.slick.lifted._
  import scala.slick.ast.{Node, Library}
  import scala.slick.lifted.Column
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
      } first)}*/


  import Macros._

  printSpacer("Day of the week persons were born")
  debugExpr {
    println(inSession { (Persons.all map { p1 => (p1.fullName, dayOfWeekName(p1.birthday)) }) list } mkString "\n")
  }

  printSpacer("Ages of all persons")
  debugExpr {
    println(inSession { (Persons.all map { p1 => (p1.fullName, diffYear(p1.birthday, DateTime.now) / 12) }) list } mkString "\n")
  }

  printSpacer("Persons and their ages")
  debugExpr {
    println(inSession { (Persons.all map { p1 => (p1.fullName, yearsFromNow(p1.birthday)) }).list } mkString "\n")
    println("Average Age: "+inSession { (Persons.all.map { p1 => (yearsFromNow(p1.birthday)) }).avg.run })
  }

  printSpacer("How many works each author produced")
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
