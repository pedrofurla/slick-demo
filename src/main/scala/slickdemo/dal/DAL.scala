package slickdemo.dal

import scala.slick.session.Session
import java.sql.Date

trait DataLayer {
  val profile: scala.slick.driver.ExtendedProfile
  val database: scala.slick.session.Database
}

object Drivers {

  case class Driver(slick: scala.slick.driver.ExtendedProfile, jdbc: String)

  val mysql = Driver(scala.slick.driver.MySQLDriver, "com.mysql.jdbc.Driver")
  val h2 = Driver(scala.slick.driver.H2Driver, "org.h2.Driver")
  val postgresql=Driver(scala.slick.driver.PostgresDriver, "org.postgresql.Driver")
  val access=Driver(scala.slick.driver.AccessDriver, "sun.jdbc.odbc.JdbcOdbcDriver")
  val sqlserver=Driver(scala.slick.driver.SQLServerDriver, "net.sourceforge.jtds.jdbc.Driver")
  // Todo Missing:
  // sqlite
  // hsqldb
  // derby
  // db2 ?
  // oracle ?

}

class MysqlLayer extends DataLayer {
  val profile = Drivers.mysql.slick
  val database = profile.simple.Database.forURL(s"jdbc:mysql://localhost/slick-demo?user=root", driver = Drivers.mysql.jdbc)
}

class H2FileLayer extends DataLayer {
  val profile = Drivers.h2.slick
  val database = profile.simple.Database.forURL("jdbc:h2:tcp://localhost/db/slick-demo;IFEXISTS=TRUE", driver = Drivers.h2.jdbc)
}

class H2Layer extends DataLayer {
  val profile = Drivers.h2.slick
  val database = profile.simple.Database.forURL("jdbc:h2:mem:slick-demo;DB_CLOSE_DELAY=-1", driver = Drivers.h2.jdbc)
}

/** Data access layer */
object DAL {
  val dataLayer: DataLayer = if (System.getProperty("MYSQL_ENV") == null || System.getProperty("MYSQL_PORT") != null) {
    //new MysqlLayer
    new H2Layer
  } else  {
    new H2Layer
  }

  type ID = Int
  type PK = Option[Int]
  type SINTERVAL = (java.sql.Date, java.sql.Date)
  type SDATE = java.sql.Date
  type JDATE = org.joda.time.DateTime
  type JINTERVAL = org.joda.time.Interval

  implicit def slickSession: Session = dataLayer.profile.simple.Database.threadLocalSession

  def inSession[T](t: => T): T = dataLayer.database.withSession[T] { t }

  def withSession[T](t: Session => T): T = dataLayer.database.withSession[T] { implicit s: Session => t(s) }

  //todo stuff that should probably be elsewhere

  import dataLayer.profile.simple._
  import org.joda.time._

  implicit def date2dateTime = MappedTypeMapper.base[DateTime, Date](
    dateTime => new Date(dateTime.getMillis),
    date => new DateTime(date)
  )
  // TODO this shouldn't be here
  lazy val schema: List[Table[_]] = List(Authors,Books, BookAuthors, Persons)
}
 