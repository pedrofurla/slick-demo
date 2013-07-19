package slickdemo.dal

import java.sql.Date

/**
 * Created with IntelliJ IDEA.
 * User: pedrofurla
 * Date: 17/07/13
 * Time: 23:29
 */
trait JodaTimeSupport { this: SlickSupport =>
  import slickSupport.ql._
  import org.joda.time._

  def dateTime(year: Int, month: Int, day: Int) = new DateTime(year, month, day, 0, 0, 0)

  /* Custom data types usage */
  implicit def date2dateTime = MappedTypeMapper.base[DateTime, Date](
    dateTime => new Date(dateTime.getMillis),
    date => new DateTime(date)
  )

}
