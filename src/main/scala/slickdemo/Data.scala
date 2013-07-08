package slickdemo

import slickdemo.dal._
import slickdemo.dal.DAL._

object Data {

  def authorship(book: Book, authors: Author*) = inSession {
    val b = Books.autoInc.insert(book)
    val as = Authors.autoInc.insertAll(authors: _*)
    BookAuthors.oneToMany(b.id.get, as.map(_.id.get).toList) // smelly .get, twice!!
    (b, as)
  }

  /*
  authorship(
      Book(None, "", ""),
      Author(None, ""))
   */
  def insertBooks = {
    List(
      authorship(
        Book(None, "Functional Programming in Scala", ""),
        Author(None, "Paul Chiusano"),
        Author(None, "Rúnar Bjarnason")),
      authorship(
        Book(None, "Types and Programming Languages", ""),
        Author(None, "Benjamin Pierce")),
      authorship(
        Book(None, "Introduction to Computation Theory", ""),
        Author(None, "Michael Sipser")),
      authorship(
        Book(None, "Programming in Scala", ""),
        Author(None, "Martin Odersky"),
        Author(None, "Lex Spoon"),
        Author(None, "Bill Venners")),
      authorship(
        Book(None, "Atomic Scala", ""),
        Author(None, "Bruce Eckel")),
      authorship(
        Book(None, "Concepts of Programming Languages", ""),
        Author(None, "Robert W. Sebesta")),
      authorship(
        Book(None, "Scala in Depth", ""),
        Author(None, "Josh Suereth")),
      authorship(
        Book(None, "Akka in Action", ""),
        Author(None, "Raymond Roestenburg"),
        Author(None, "Rob Bakker"),
        Author(None, "Rob Williams")),
      authorship(
        Book(None, "How to Bake your Cakes in Scala", ""),
        Author(None, "Daniel Spiewak")),
      authorship(
        Book(None, "Scala em Português", ""),
        Author(None, "Daniel Sobral"))
    )

  }

  import org.joda.time._
  def dateTime(year:Int, month:Int, day:Int) = new DateTime(year,month,day,0,0,0)

  def insertPersons = inSession {
    Persons.autoInc.insertAll(
      Person(None, "Pedro Furlanetto", dateTime(1977, 11, 4), "pfurla", ""),
      Person(None, "Dilbert Adams", dateTime(1999, 1, 20), "dadams", ""),
      Person(None, "Alfred Molina", dateTime(1953, 5, 24), "amolina", ""),
      Person(None, "Stewie Griffin", dateTime(1999, 1, 31), "sgriffin", ""),
      Person(None, "Lisa Simpson", dateTime(1989, 12, 17), "lsimpson", "") //,
      //Person(None, "", new DateTime(), "", "")
      //Person(None, "", new DateTime(), "","")
    )
  }
}
