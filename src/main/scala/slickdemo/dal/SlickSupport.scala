package slickdemo.dal

import scala.slick.session.Session
import java.sql.Date
import slickdemo.Data

trait SlickSupport {
  val driver: scala.slick.driver.ExtendedDriver
  /**
    The class scala.slickDriver.driver.BasicProfile.SimpleQL aggregates
      - Table
      - Session
      - Database
      - scala.slickDriver.driver.BasicProfile.Implicits

    These are the most the entry points for clients of Slick.
  */
  lazy val ql:driver.SimpleQL = driver.simple // lazy here to avoid init problems...
  val database: scala.slick.session.Database
}

object Drivers {

  case class Driver(slickDriver: scala.slick.driver.ExtendedDriver, jdbc: String)

  val mysql = Driver(scala.slick.driver.MySQLDriver, "com.mysql.jdbc.Driver")
  val h2 = Driver(scala.slick.driver.H2Driver, "org.h2.Driver")
  val postgresql = Driver(scala.slick.driver.PostgresDriver, "org.postgresql.Driver")
  val access = Driver(scala.slick.driver.AccessDriver, "sun.jdbc.odbc.JdbcOdbcDriver")
  val sqlserver = Driver(scala.slick.driver.SQLServerDriver, "net.sourceforge.jtds.jdbc.Driver")
  // Todo Missing:
  // sqlite
  // hsqldb
  // derby
  // db2 ?
  // oracle ?

}

class MysqlSupport extends SlickSupport {
  val driver = Drivers.mysql.slickDriver
  val database = driver.simple.Database.forURL(s"jdbc:mysql://localhost/slickDriver-demo?user=root", driver = Drivers.mysql.jdbc)
}

class H2FileSupport extends SlickSupport {
  val driver = Drivers.h2.slickDriver
  val database = driver.simple.Database.forURL("jdbc:h2:tcp://localhost/db/slickDriver-demo;IFEXISTS=TRUE", driver = Drivers.h2.jdbc)
}

class H2Support extends SlickSupport {
  val driver = Drivers.h2.slickDriver
  val database = driver.simple.Database.forURL("jdbc:h2:mem:slickDriver-demo;DB_CLOSE_DELAY=-1;IGNORECASE=TRUE;DATABASE_TO_UPPER=FALSE", driver = Drivers.h2.jdbc)
}

/** Data access layer - warning: it's full of utility here only to prevent more imports - silly me...*/
object SlickSupport extends DateTimeFunctions with JodaTimeSupport {
  val slickSupport: SlickSupport = new H2Support


  type ID = Int
  type PK = Option[ID]
  type SINTERVAL = (java.sql.Date, java.sql.Date)
  type SDATE = java.sql.Date
  type JDATE = org.joda.time.DateTime
  type JINTERVAL = org.joda.time.Interval

  implicit def slickSession: Session = slickSupport.ql.Database.threadLocalSession

  def inSession[T](t: => T): T = slickSupport.database.withSession[T] { t }

  def withSession[T](t: Session => T): T = slickSupport.database.withSession[T] { implicit s: Session => t(s) }

  //todo stuff that should probably be elsewhere

  import slickSupport.ql._
  import slickdemo.domain._

  lazy val schemas: List[Table[_]] = List(Authors, Books, BookAuthors, Persons)

  private[this] var ready = false
  def init = {
    if (!ready) {
      createDb
      Data.insertBooks
      Data.insertPersons
      ready = true
    }
  }

  def createDb = inSession {
    schemas foreach { _.ddl.create }
    println("Database created")
  }

  def printSpacer(title:String="") = println("\n---------- "+title+" ----------")
}

