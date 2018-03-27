package org.tmt.macros

import java.util.concurrent.Executors

import scala.concurrent.ExecutionContext
import scala.language.experimental.macros
import scala.reflect.macros.blackbox

object AsyncMacros {
  val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  def spawn[T: c.WeakTypeTag](c: blackbox.Context)(body: c.Expr[T]) = {
    import c.universe._
    q"_root_.scala.async.Async.async($body)(${reify(ec)})"
  }

  def await(c: blackbox.Context) = {
    import c.universe._
    val arg = c.prefix.tree.asInstanceOf[Apply].args.head
    q"_root_.scala.async.Async.await($arg)"
  }
}
