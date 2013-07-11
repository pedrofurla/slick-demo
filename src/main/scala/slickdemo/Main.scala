package slickdemo

import dal._
import DAL._
import dataLayer.profile.simple._
import org.joda.time.DateTime


object Main {

  def printSpacer = println("---------------------")



  def main(args: Array[String]): Unit = {

    sqls

    init

    inSession { println(Books.all.list mkString "\n") }

    println("All persons: ")
    inSession { println(Persons.all.list mkString "\n") }

    println("Random ad hoc queries:")
    inSession {
      println("  Idade média das pessoas: ")
      //Persons.all.map {p => (org.joda.time.DateTime - p.birthday).avg }
      //println("Younger person was born: "+ (Persons.all.map { p => p.birthday}.max))
      println("Younger person was born: " + Persons.map(_.birthday.max).first)
      println("Older person was born: " + Persons.map(_.birthday.min).first)
      //println("Date differences: "+  Persons.map(p =>p.birthday.max.get - p.birthday.min.get).first )
      //inSession { Persons.map(p => (p.fullName,Functions.currentDate)).first } // works

    }

  }

  def init = {
    createDb
    val newBooks = Data.insertBooks
    val persons = Data.insertPersons
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