package slickdemo

import dal._
import domain._
import SlickSupport._
import slickSupport.ql._
import Macros._

object Main {

  init

  def main(args: Array[String]): Unit = {

    printSpacer("Lifted embedding Slick basic quering")

    println

    printSpacer("Some lifted embedding Slick queries and their SQL translations")
    sqls

    printSpacer("All books")
    debugExpr{ inSession { println(Books.all.list mkString "\n") } }

    printSpacer("All persons")
    debugExpr{ inSession { println(Persons.all.list mkString "\n") } }

    printSpacer("Ad hoc queries:")
    debugExpr {
      inSession {
        println("Younger person was born: " + Persons.map(_.birthday).max.run)
        println("Older person was born: " + Persons.map(_.birthday).min.run)
      }
    }

  }

  def sqls = {
    import Macros._

    def show: PartialFunction[(String, String), Unit] = {
      case (expr, source) => println(s"`$expr` generates: \n $source \n ${List.fill(10)("-").mkString}")
    }

    val qs = List(
      sourceExpr(Books.likeTitle("Some title").map { _.* }.selectStatement),
      sourceExpr(Books.byAuthor(999L).map { _.* }.selectStatement),
      sourceExpr(Authors.likeName("Some author").map { _.* }.selectStatement),
      sourceExpr(Authors.byBookName("Some title").map { _.* }.selectStatement)
    )
    qs foreach show
  }

}