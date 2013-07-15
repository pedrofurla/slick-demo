package slickdemo

//import dal._

import dal.DAL._
import dal._
import scala.slick.jdbc.SetParameter
import scala.slick.session.{PositionedResult, PositionedParameters}
import org.joda.time.DateTime
//import dataLayer.profile.simple._

/**
 * Created with IntelliJ IDEA.
 * User: pedrofurla
 * Date: 10/07/13
 * Time: 15:44
 * To change this template use File | Settings | File Templates.
 */
object SqlQueries extends App {

  import scala.slick.jdbc.{GetResult, StaticQuery => Q}

  init

  import Macros._

  printSpacer("Custom data type for parameters")
  debugExpr {
    implicit object SetJodaDateTime extends SetParameter[JDATE] {
      def apply(v: JDATE, pp: PositionedParameters) { pp.setDate(new java.sql.Date(v.getMillis)) }
    }

    // Q.u + "" is odd
    def insertPerson(p: Person) =
      Q.u + "INSERT INTO person (full_name, birthday, login, password) VALUES(" +
        "" +? p.fullName +
        "," +? p.birthday +
        "," +? p.login +
        "," +? p.password + ")"

    val stmt = insertPerson(Person(None, "John Nash", dateTime(1945, 10, 10), "", ""))

    inSession { stmt.execute }
  }

  // these source repetitions are annoying me
  implicit object SetJodaDateTime extends SetParameter[JDATE] {
    def apply(v: JDATE, pp: PositionedParameters) { pp.setDate(new java.sql.Date(v.getMillis)) }
  }

  printSpacer("Custom data type for extracting results")
  debugExpr {
    implicit object GetJodaDate extends GetResult[JDATE] {
      def apply(rs: PositionedResult) = new DateTime(rs.nextDate().getTime)
    }
    implicit val getPersonResult = GetResult(r => Person(Option(r.<<), r.<<, r.<<, r.<<, r.<<))
  }

  import org.joda.time.DateTime

  implicit object GetJodaDate extends GetResult[JDATE] {
    def apply(rs: PositionedResult) = new DateTime(rs.nextDate().getTime)
  }

  implicit val getPersonResult =
    GetResult(r => Person(Option(r.<<), r.<<, r.<<, r.<<, r.<<))

  printSpacer("Automatically extracting Person:")
  debugExpr {
    inSession { Q.queryNA[Person]("select * from PERSON") foreach println }
  }

  printSpacer("Two different ways to declare queries and use parameter")
  debugExpr {
    val bornBefore = Q.query[JDATE, Person]("select * from person where birthday < ?")
    inSession { bornBefore.list(dateTime(1988, 1, 1)) foreach println }
  }

  println

  debugExpr {
    val personByName = Q[String, Person] + "select * from person where full_name = ?"
    println { inSession { personByName("John Nash").firstOption } }
  }

  printSpacer("Using string interpolator to create queries")
  debugExpr {
    import Q.interpolation
    def personById(id: Long) = sql"select * from person where id=$id".as[Person]
    // sqlu is the update/delete/dml version of sql interpolator

    inSession { println(s"Same person: ${personById(3).first}}") }

    println

    println("Authors and how many works produced by each: ")
    inSession {
      sql"""
        select count(a.id), a.name
        from book_authors ba inner join author a on ba.author_id = a.id
        group by a.id, a.name order by a.name
      """.as[(Long, String)].list foreach println
    }
  }


}
