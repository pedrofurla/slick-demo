package slickdemo.dal

/**
 * Created with IntelliJ IDEA.
 * User: pedrofurla
 * Date: 17/07/13
 * Time: 06:36
 */
trait DateTimeFunctions { this: SlickSupport.type =>
  import dataLayer.ql._

  import org.joda.time._
  import scala.slick.lifted.{ConstColumn, SimpleFunction, Column}

  /* Basic usage of custom functions -- This instance is specific of H2, it's possible it will work with MySql */
  def diffYear(a: Column[JDATE], b: Column[JDATE]) = SimpleFunction[Int]("DATEDIFF").apply(Seq(ConstColumn("MONTH"), a, b))
  def yearsFromNow(c: Column[JDATE]) = diffYear(c, DateTime.now) / 12
  def dateDiff(part:String)(a: Column[JDATE], b: Column[JDATE]) =
    SimpleFunction[Int]("DATEDIFF").apply(Seq(ConstColumn(part), a, b))
  def dateAdd(part:String)(a: Column[JDATE], b: Column[JDATE]) =
    SimpleFunction[Int]("DATEADD").apply(Seq(ConstColumn(part), a, b))

  /*
  Sql functions for dealing with DateTime column type.
  None of these are part of the SQL Standard and are meant to only work with H2

  References
  - H2         http://www.h2database.com/html/functions.html
  - Mysql      http://dev.mysql.com/doc/refman/5.5/en/date-and-time-functions.html
  - Postgresql http://www.postgresql.org/docs/8.1/static/functions-datetime.html
  - Derby      http://db.apache.org/derby/docs/10.2/ref/rrefsqlj29026.html
  - SQL Server http://msdn.microsoft.com/en-us/library/ms186724.aspx
  - Oracle     http://psoug.org/reference/timestamp.html and
               http://psoug.org/reference/date_func.html

  https://docs.google.com/spreadsheet/ccc?key=0Au6wCO3e3igndHk1OEJtSm56SE5jWDJrN1hDYVlZWVE#gid=0

  */
  // template def f(c: Column[JDATE]) = SimpleFunction[String]("").apply(Seq(c))
  def dayOfWeekName(c: Column[JDATE]) = SimpleFunction[String]("DAYNAME").apply(Seq(c))

  def monthName(c: Column[JDATE]) = SimpleFunction[String]("MONTHNAME").apply(Seq(c))

  def dayOfWeek(c: Column[JDATE]) = SimpleFunction[String]("DAY_OF_WEEK").apply(Seq(c))
  def dayOfYear(c: Column[JDATE]) = SimpleFunction[String]("DAY_OF_YEAR").apply(Seq(c))

  def year(c: Column[JDATE]) = SimpleFunction[String]("YEAR").apply(Seq(c))
  def month(c: Column[JDATE]) = SimpleFunction[String]("MONTH").apply(Seq(c))
  def day(c: Column[JDATE]) = SimpleFunction[String]("DAY_OF_MONTH").apply(Seq(c))

  def hour(c: Column[JDATE]) = SimpleFunction[String]("HOUR").apply(Seq(c))
  def minute(c: Column[JDATE]) = SimpleFunction[String]("MINUTE").apply(Seq(c))
  def second(c: Column[JDATE]) = SimpleFunction[String]("SECOND").apply(Seq(c))

}
