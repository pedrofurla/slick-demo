package slickdemo.dal

import SlickSupport._
import slickSupport.ql._

abstract class BaseTable[T](_schemaName: Option[String], _tableName: String) extends Table[T](_schemaName, _tableName) {
  def this(_tableName: String) = this(None, _tableName)

  def nullOrNot(nullable: Boolean) = if (nullable) O.Nullable else O.NotNull
  def autoIncPk(name: String) = column[ID](name, O.PrimaryKey, O.AutoInc)
  // the ideal here is receive name and  `options: ColumnOption[C]*`
  // and some how have it not null
  def string(name: String, nullable: Boolean = false) = column[String](name, nullOrNot(nullable))
  def int(name: String, nullable: Boolean = false) = column[Int](name, nullOrNot(nullable))
  def long(name: String, nullable: Boolean = false) = column[Long](name, nullOrNot(nullable))
  def date(name: String, nullable: Boolean = false) = column[SDATE](name, nullOrNot(nullable))
  def boolean(name: String, nullable: Boolean = false) = column[Boolean](name, nullOrNot(nullable))

  def disabled = column[Boolean]("disabled", O.NotNull, O.Default(false))

  // TODO move to utils
  def now = new java.sql.Date((new java.util.Date).getTime)
  implicit class date2sql(val d: java.util.Date) {
    def sql = new java.sql.Date(d.getTime)
  }

  def all = Query[this.type, T, this.type](this)

  import scala.slick.lifted._

  def equalBy[B: BaseTypeMapper](proj: this.type => Column[B]): B => Query[this.type, T] = {
    (str: B) =>
      Query[this.type, T, this.type](this) where { (x => proj(x) === str) }
  }

}