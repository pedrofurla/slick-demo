package slickdemo

import dal._
import DAL._
import dataLayer.profile.simple._


object Main {

  def main(args: Array[String]): Unit = {

    sqls

    createDb

    val newBooks = Data.insertBooks

    inSession { println(Books.all.list mkString "\n") }

    val persons = Data.insertPersons

    inSession { println(Persons.all.list mkString "\n")}

  }

  def createDb = inSession {
    schema foreach { _.ddl.create }
    println("Database created")
  }

  def sqls = {
    import Macros._

    def show: PartialFunction[(String, String), Unit] = {
      case (expr, source) => println(s"$expr: \n $source \n ${List.fill(10)("-").mkString} generates")
    }

    //val x = Books.likeTitle("Some title").map { _.* }

    val qs = List(
      sourceExpr(Books.likeTitle("Some title").map { _.* }.selectStatement),
      sourceExpr(Books.byAuthor(999).map { _.* }.selectStatement),
      sourceExpr(Authors.likeName("Some author").map { _.* }.selectStatement),
      sourceExpr(Authors.byBookName("Some title").map { _.* }.selectStatement)
    )
    qs foreach show


  }

}