package slickdemo

import domain._
import slickdemo.dal.SlickSupport._
import slickdemo.dal.SlickSupport.slickSupport.ql._
import scala.slick.lifted.Parameters
import scala.slick.driver.BasicQueryTemplate

/**
 * Created with IntelliJ IDEA.
 * User: pedrofurla
 * Date: 14/07/13
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
object ParameterQueries extends App {

  init

  // ------ These were my original attempts, all failed ----- //

  // Types were annotated only as a source of information.

  printSpacer("All books:")
  // Proves the data is there
  inSession { println(Books.all.list mkString "\n") }

  printSpacer("Scala books - likeTite")
  println(Books.likeTitle("Scala").selectStatement)
  inSession {
    println(Books.likeTitle("Scala").list mkString "\n")
  }


  printSpacer("Parameter to column:")
  // Too much typing to an outcome that is basically a column replaced with a parameter.'
  val ptitle: BasicQueryTemplate[String, Book] = for (x <- Parameters[String]; b <- Books.likeTitle(x)) yield b
  println(s"Select statement=${ptitle.selectStatement}")

  printSpacer()
  inSession {
    println(ptitle("Scala").list mkString "\n")
  }

  //Main.printSpacer
  // A little better but with wrong type
  //val ptitle2: BasicQueryTemplate[String, Nothing] = Parameters[String] map { Books.likeTitle2 _ }
  // doesn't compile with
  // "Don't know how to unpack Query[Books.type,Book] to QU and pack to Any"

  // BTW, I don't understand `map` and `flatMap` here, isn't it natural transformations? `F[A] => G[A]` ?.

  //Main.printSpacer
  //println(ptitle2("Akka").list mkString "\n")

  printSpacer()
  // A little better and with the right type
  val ptitle3: BasicQueryTemplate[String, Book] = Parameters[String] flatMap { Books.likeTitle _ }
  println(s"Select statement=${ptitle3.selectStatement}")
  inSession {
    println(ptitle3("Akka").list mkString "\n")
  }

  // Given the last likeTitle2, I bet we can easily come with something very simple that does
  // (Column[A] => Query[Table.type,X]) => BasicQueryTemplate[A, X], if there isn't one.

  // ------------ Another Attempt

  printSpacer()
  println("All Scala:")
  inSession { println(Books.likeTitle("Scala").map { _.* }.list mkString "\n") }

  printSpacer()
  // Shows something is wrong when trying to give a parameter to a function functions that expects a column
  val queryLikeTitle = for (p <- Parameters[String]; b <- Books.likeTitle(p)) yield b
  inSession { println(queryLikeTitle("Scala").list mkString "\n") }


}
