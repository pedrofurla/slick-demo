package slickdemo.dal

import slickdemo.dal.DAL._
import dataLayer.profile.simple._

case class Book(id: PK, title: String, synopsis: String) {
  //
  lazy val authors:List[Author] = Authors.byBook(id.get).list // I know, id.get here is a code smell // TODO
}
object Book {
  def likeTitle(title:String):List[Book] = Books.likeTitle(title).list
  def byAuthorName(name:String):List[Book] = Books.byAuthorName(name).list
}

case class Author(id: PK, name: String) {
  lazy val books:List[Book] = Books.byAuthor(id.get).list // Yeah yeah, smelly // TODO
}
object Author {
  def likeName(name: String) = Authors.likeName(name).list
}

object Books extends BaseTable[Book]("book") {
  def id = autoIncPk("id")
  def title = string("title")
  def synopsis = string("synopsis")

  def * = id.? ~ title ~ synopsis <>(Book.apply _, Book.unapply _)

  def autoInc = * returning id.? into {
    case (Book(_, a, b), id) => Book(id, a, b)
  }

  def autoInc2 = title ~ synopsis returning id.? into {
    case (m, id) => Function.uncurried(((Book.apply _).curried(id))).tupled(m)
  }

  def byId = equalBy { _.id }

  // todo check Authors.likeName todo
  def likeTitle(t: String) = for { b <- all if b.title.toLowerCase like "%" + t.toLowerCase + "%"  } yield b

  def likeTitle2(t: Column[String]) = for { b <- all if b.title.toLowerCase like "%" + t.toLowerCase + "%"  } yield b

  def titleLike(pattern:Column[String]):Column[Boolean] = title.toLowerCase like pattern.toLowerCase

  def byAuthor(aid:Int) =
    for {
      ba <- BookAuthors.all if ba.authorId === aid
      b <- ba.bookFk
    } yield b

  def byAuthorName(name:String) =
    for {
      a <- Authors.likeName(name)
      ba <- BookAuthors.all if ba.authorId === a.id
      b <- ba.bookFk
    } yield b

  def byAuthorName2(name: String) =
    for {
      (a, ba) <- Authors.likeName(name) innerJoin BookAuthors.all on { _.id === _.authorId }
      b <- Books.all if ba.bookId === b.id
    } yield b


  def byAuthorName3(name:String) =
    for { // it would be nice if case wasn't need here
      ((a, ba), b) <- Authors.likeName(name) innerJoin
                    BookAuthors.all innerJoin
                    Books.all on {
        case ((a,ba),b) => a.id === ba.authorId && b.id === ba.authorId
     }
    } yield b

  def byAuthorName4(name:Column[String]) =
    for {// it would be nice if case wasn't need here
      ((a, ba), b) <- Authors.likeName2(name) innerJoin
        BookAuthors.all innerJoin
        Books.all on {
        case ((a, ba), b) => a.id === ba.authorId && b.id === ba.authorId
      }
    } yield b
}

object Authors extends BaseTable[Author]("author") {
  def id = autoIncPk("id")
  def name = string("name")

  def * = id.? ~ name <> (Author.apply _, Author.unapply _)

  def autoInc = * returning id.? into {
    case (Author(_, a), id) => Author(id, a)
  }

  def autoInc2 = name returning id.? into {
    case (m, id) => Author.apply(id, m)
  }

  def byId = equalBy { _.id }
  def byBook(bid:Int) =
    for{
      ba <- BookAuthors.all if ba.bookId === bid
      a <- ba.authorFk
    } yield a

  def byBookName(name: String) =
    for {
      b <- Books.likeTitle(name)
      ba <- BookAuthors.all if ba.bookId === b.id
      a <- ba.authorFk
    } yield a

  def byBookName2(name: String) =
    for {
      (b, ba) <- Books.likeTitle(name) innerJoin BookAuthors.all on { _.id === _.bookId }
      if ba.bookId === b.id
      a <- ba.authorFk
    } yield a


  // todo create a string function for "contains" ?
  // todo abstract a "likeBy" like "equalBy" ?
  def likeName(n: String) = for { a <- all if a.name like "%" + n + "%"} yield a

  def likeName2(n: Column[String]) = for { a <- all if a.name like "%" + n + "%"} yield a
}

object BookAuthors extends BaseTable[(Int, Int)]("book_authors") {
  def bookId = int("book_id")
  def authorId = int("author_id")

  // todo plural or not?
  def bookFk = foreignKey(tableName + "_book_fk", bookId, Books)(_.id)
  def authorFk = foreignKey(tableName + "_author_fk", authorId, Authors)(_.id)

  def pk = primaryKey(tableName+"_pk", (authorId, bookId))

  def bookIdx = index(tableName+"_book_id_idx", bookId, unique = false)
  def authorIdx = index(tableName+"_author_id_idx", authorId, unique = false)

  def * = bookId ~ authorId

  def oneToMany(bId:Int, aIds:List[Int])(implicit s:scala.slick.session.Session) =
    *.insertAll( aIds map { (bId, _) } :_* )(s)
}