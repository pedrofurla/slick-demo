package slickdemo

import dal._
import DAL._
import dataLayer.profile.simple._

object Main {

  init

  def main(args: Array[String]): Unit = {

    printSpacer("Some queries and their translations")
    sqls

    printSpacer("All books")
    inSession { println(Books.all.list mkString "\n") }

    printSpacer("All persons")
    inSession { println(Persons.all.list mkString "\n") }

    println("Random ad hoc queries:")
    inSession {
      println("Younger person was born: " + Persons.map(_.birthday).max.run)
      println("Older person was born: " + Persons.map(_.birthday).min.run)
    }

  }

  def sqls = {
    import Macros._

    def show: PartialFunction[(String, String), Unit] = {
      case (expr, source) => println(s"`$expr` generates: \n $source \n ${List.fill(10)("-").mkString}")
    }

    val qs = List(
      sourceExpr(Books.likeTitle("Some title").map { _.* }.selectStatement),
      sourceExpr(Books.byAuthor(999).map { _.* }.selectStatement),
      sourceExpr(Authors.likeName("Some author").map { _.* }.selectStatement),
      sourceExpr(Authors.byBookName("Some title").map { _.* }.selectStatement)
    )
    qs foreach show
  }

}