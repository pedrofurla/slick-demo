package slickdemo.dal

import DAL._
import dataLayer.profile.simple._
import java.sql.Date
import org.joda.time.DateTime


case class Person(id:PK, fullName:String, birthday: JDATE, login:String, password:String)

object Persons extends BaseTable[Person]("person") {
  def id = autoIncPk("id")
  def fullName = string("full_name")
  def birthday = column[JDATE]("birthday", O.NotNull)
  def login = string("login")
  def password = string("password")

  def * = id.? ~ fullName ~ birthday ~ login ~ password <>(Person.apply _, Person.unapply _)

  def credentials = login ~ password
  def personal = fullName ~ birthday

  // bogun won't compile
  //def allData = id.? ~ (credentials ~: personal)
  //def noId2 = credentials ~: personal

  def noId = fullName ~ birthday ~ login ~ password
  // good, Projection5
  def allData2 = id.? ~: noId
  allData2

  import scala.slick.lifted._
  def allData3 = new Projection5(id.?, fullName, birthday, login, password)

  def autoInc = * returning id.? into {
    case (Person(_, a, b,c ,d), id) => Person(id, a, b, c, d)
  }


  def autoInc2 = fullName ~ birthday ~ login ~ password returning id.? into {
    case (m, id) => Function.uncurried(((Person.apply _).curried(id))).tupled(m)
  }

  def byId = equalBy { _.id }

  val eighteenYearsMillis = 1000 * 60 * 60 * 24 * 365 * 18 // millesecs in 18 years
  def adulthoodTime = new DateTime(now.getTime - eighteenYearsMillis)

  def underages = for { p <- all if p.birthday < adulthoodTime } yield p
  def adults   = for { p <- all if p.birthday >= adulthoodTime } yield p

  /*def branch = for {
      p <- Parameters[Int]
      s <- HotSites where { _.id === p }
      b <- s.branchFk
  } yield b*/


}
 
