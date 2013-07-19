package slickdemo.experiments

import scala.language.implicitConversions
import slickdemo.dal._
import slickdemo.dal.SlickSupport._
import slickSupport.ql._
import slickdemo.domain._

object WithDomain {

  implicit class PimpedPersons(p: Persons.type) {
    import p._

    def credentials = login ~ password
    def personal = fullName ~ birthday

    // bogus won't compile
    //def allData = id.? ~ (credentials ~: personal)
    //def noId2 = credentials ~: personal

    def noId = fullName ~ birthday ~ login ~ password
    // good, Projection5
    def allData2 = id.? ~: noId

    import scala.slick.lifted._

    def allData3 = new Projection5(id.?, fullName, birthday, login, password)

  }

  import scala.slick.lifted._
  import scala.slick.ast._
  class StringColumnExtensionMethods2[P1](val c: Column[P1]) extends AnyVal with ExtensionMethods[String, P1] {
    def +|[P2, R](e: Column[P2])(implicit om: o#arg[String, P2]#to[String, R]) =
      om(Library.Concat.column[String](n, Node(e)))
  }

  implicit def stringColumnExtensionMethods2(c: Column[String])(implicit tm: BaseTypeMapper[String])
  = new StringColumnExtensionMethods2[String](c)


  implicit class PimpedColumnString(c:Column[String]) {
    def contains(s:Column[String]):Column[Boolean] = c like ConstColumn("%") ++ s ++ "%"
  }

  implicit class PimpedBooks(b: Books.type) {

    def autoInc = Books.* returning Books.id.? into {
      case (Book(_, a, b), id) => Book(id, a, b)
    }


    // broken, raw string concatenation in contrast with Column[String] concatenation
    def likeTitle0(t: Column[String]) = for {b <- Books.all if b.title.toLowerCase like "%" + t.toLowerCase + "%"} yield b

    //implicit class pimping(c:Column[String])

    //import scala.slick.lifted.{ConstColumn => CC}
    // the valueToConstColumn doesn't get triggered here
    //def likeTitle3(t: Column[String]) = for { b <- Books.all if b.title.toLowerCase like "%" +| t.toLowerCase +| "%"  } yield b

    // proof it has little to do with the fact that '++' is a valid string method
    //def likeTitle3(t: Column[String]) = for { b <- Books.all if b.title.toLowerCase like "%" +| t.toLowerCase } yield b
    def likeTitle3_1(t: Column[String]) = for { b <- Books.all if b.title.toLowerCase like t.toLowerCase +| "%" } yield b

    // valueToConstColumn only gets triggered if on the rhs.
    def likeTitle4(t: Column[String]) = for {b <- Books.all if b.title.toLowerCase like t.toLowerCase ++ "%"} yield b

    // here gets the famous "required: GenTraversableOnce[...]"
    //def likeTitle5(t: Column[String]) = for {b <- Books.all if b.title.toLowerCase like "%" ++ t.toLowerCase } yield b

    // here a work around, kind of strange to say the least
    def likeTitle6(t: Column[String]) =
      for {
        b <- Books.all
        if b.title.toLowerCase like ConstColumn("%") ++ t.toLowerCase ++ "%" } yield b

    // now using the contains method
    def likeTitle7(t: Column[String]) = for {b <- Books.all if b.title.toLowerCase contains t.toLowerCase} yield b

    def byAuthorName2(name: String) =
      for {
        (a, ba) <- Authors.likeName(name) innerJoin BookAuthors.all on { _.id === _.authorId }
        b <- Books.all if ba.bookId === b.id
      } yield b


    def byAuthorName3(name: String) =
      for {// it would be nice if case wasn't need here
        ((a, ba), b) <- Authors.likeName(name) innerJoin
          BookAuthors.all innerJoin
          Books.all on {
          case ((a, ba), b) => a.id === ba.authorId && b.id === ba.authorId
        }
      } yield b

    def byAuthorName4(name: Column[String]) =
      for {// it would be nice if case wasn't need here
        ((a, ba), b) <- Authors.likeName2(name) innerJoin
          BookAuthors.all innerJoin
          Books.all on {
          case ((a, ba), b) => a.id === ba.authorId && b.id === ba.authorId
        }
      } yield b
  }

  implicit class PimpedAuthors(a:Authors.type) {

    def byBookName2(name: String) =
      for {
        (b, ba) <- Books.likeTitle(name) innerJoin BookAuthors.all on { _.id === _.bookId }
        if ba.bookId === b.id
        a <- ba.authorFk
      } yield a

    def likeName2(n: Column[String]) = for {a <- Authors.all if a.name like "%" + n + "%"} yield a
  }

}



