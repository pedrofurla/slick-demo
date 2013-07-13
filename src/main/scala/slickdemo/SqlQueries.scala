package slickdemo

//import dal._
import dal.DAL._
import scala.slick.jdbc.SetParameter
import scala.slick.session.{PositionedResult, PositionedParameters}

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
  import dal.Person

  Main.init


  implicit object SetJodaDate extends SetParameter[JDATE] {
    def apply(v: JDATE, pp: PositionedParameters) { pp.setDate(new java.sql.Date(v.getMillis)) } }

  // Q.u + "" is odd
  def insertPerson(p:Person) =
    Q.u+"INSERT INTO person (full_name, birthday, login, password) VALUES(" +
      ""  +? p.fullName +
      "," +? p.birthday +
      "," +? p.login    +
      "," +? p.password + ")"

  val stmt = insertPerson(Person(None, "John Nash", dateTime(1945, 10, 10), "",""))

  inSession { stmt.execute }

  import org.joda.time.DateTime

  implicit object GetBigDecimal extends GetResult[JDATE] {
    def apply(rs: PositionedResult) = new DateTime(rs.nextDate().getTime) }

  implicit val getPersonResult = GetResult(r =>
    Person(Option(r.<<), r.<<, r.<<, r.<<, r.<<)) //

  println("All persons:")
  inSession { Q.queryNA[Person]("select * from PERSON") foreach println }

  Main.printSpacer
  val bornBefore = Q.query[JDATE, Person]("select * from person where birthday < ?")

  inSession{ bornBefore.list(dateTime(1988,1,1)) foreach println }

  Main.printSpacer
  val personByName = Q[String,Person] + "select * from person where full_name = ?"

  val aPerson = inSession{ personByName("John Nash").firstOption }
  println(aPerson)

  import Q.interpolation

  // sqlu the update/delete/dml version of sql

  inSession { println(s"Same person: ${aPerson map {p => personById(p.id.get).first }}") }

  inSession {
    sql"""
      select count(a.id), a.name
      from book_authors ba inner join author a on ba.author_id = a.id
      group by a.id, a.name order by a.name
    """.as[(Long,String)].list foreach println
  }



}
