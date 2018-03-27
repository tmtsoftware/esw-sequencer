package org.tmt.macros

import java.util.concurrent.Executors

import scala.async.internal.{AsyncBase, ScalaConcurrentFutureSystem}
import scala.concurrent.ExecutionContext
import scala.language.experimental.macros
import scala.reflect.macros.Context

object SingleThreadedAsync extends AsyncBase {
  type FS = ScalaConcurrentFutureSystem.type
  val futureSystem: FS              = ScalaConcurrentFutureSystem
  val execContext: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  def impl[T: c.WeakTypeTag](c: Context)(body: c.Expr[T]): c.Expr[futureSystem.Fut[T]] = {
    import c.universe._
    super.asyncImpl[T](c)(body)(reify(execContext))
  }
}
