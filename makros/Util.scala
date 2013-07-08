package slickdemo

import scala.reflect.macros.Context
import scala.language.experimental.macros

object Util {

  // scala.reflect.runtime.currentMirror
  // universe.reify


  // previously based on log and logImpl
  // from https://github.com/retronym/macrocosm/blob/master/src/main/scala/com/github/retronym/macrocosm/Macrocosm.scala
  def sourceExpr[A](a: A): (String, A) = macro sourceExprImpl[A]

  def sourceExprImpl[A: c.WeakTypeTag](c: Context)(a: c.Expr[A]): c.Expr[(String, A)] = {
    import c.universe._

    val pos = c.enclosingPosition
    val src = c.enclosingUnit.source
    val portion = if (pos.isRange) new String(src.content.drop(pos.start).take(pos.end - pos.start)) else ""

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

    val pos = c.enclosingPosition
    val src = c.enclosingUnit.source

    val portion = if (pos.isRange) new String(src.content.drop(pos.start).take(pos.end - pos.start)) else ""
    val adup: Tree = a.tree.duplicate
    val expr = c.Expr[A](adup)

    val t = Block(
      ValDef(Modifiers(), newTermName("x"), TypeTree(), adup),
      Apply(Select(Ident(newTermName("scala.Predef")), newTermName("println")), List(Ident(newTermName("x")))),
      Ident(newTermName("x")))

    val const=c.Expr[String](Literal(Constant(portion)))

    reify {
      val x:A = expr.splice
      println(const.splice)
      x
    }
  }
}
