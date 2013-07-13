package slickdemo

import scala.reflect.macros.Context
import scala.language.experimental.macros

object Macros {

  // scala.reflect.runtime.currentMirror
  // universe.reify

  /** Given a Tree, extracts the source code in its range. Assumes -Yrangepos scalac argument. */
  def extractRange(t:Context#Tree):Option[String] = {
    val pos = t.pos
    val source = pos.source.content
    if(pos.isRange) Option(new String(source.drop(pos.start).take(pos.end-pos.start))) else None
  }

  /** Gives the source code of an expression and the result of the expr in a tuple */
  def sourceExpr[A](a: A): (String, A) = macro sourceExprImpl[A]

  // previously based on log and logImpl
  // from https://github.com/retronym/macrocosm/blob/master/src/main/scala/com/github/retronym/macrocosm/Macrocosm.scala
  def sourceExprImpl[A: c.WeakTypeTag](c: Context)(a: c.Expr[A]): c.Expr[(String, A)] = {
    import c.universe._

    val portion = extractRange(a.tree) getOrElse ""
    val t2 = Select(Select(Ident(newTermName("scala")), newTermName("Tuple2")), newTermName("apply"))

    // following advice from https://github.com/kevinwright/macroflection/blob/master/kernel/src/main/scala/net/thecoda/macroflection/Validation.scala
    //clone to avoid "Synthetic tree contains nonsynthetic tree" error under -Yrangepos
    val adup: Tree = a.tree.duplicate
    val tree = treeBuild.mkMethodCall(t2, List(Literal(Constant(portion)), adup))

    val expr = c.Expr[(String, A)](tree)

    expr
  }

  def debugExpr[A](a: A): A = macro debugExprImpl[A]

  def debugExprImpl[A: c.WeakTypeTag](c: Context)(a: c.Expr[A]): c.Expr[A] = {
    import c.universe._

    val portion = extractRange(a.tree) getOrElse ""
    val adup: Tree = a.tree.duplicate
    val expr = c.Expr[A](adup)

    /*
    val t = Block(
      ValDef(Modifiers(), newTermName("x"), TypeTree(), adup),
      Apply(Select(Ident(newTermName("scala.Predef")), newTermName("println")), List(Ident(newTermName("x")))),
      Ident(newTermName("x")))*/

    val const=c.Expr[String](Literal(Constant(portion)))

    reify {
      println(const.splice)
      expr.splice
    }
  }
}
