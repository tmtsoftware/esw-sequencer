package tmt.sequencer

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

object ScriptImports {
  private[tmt] val wiring = new Wiring

  lazy val D: Dsl = wiring.dsl
  lazy val E: Engine = wiring.engine

  implicit def toFuture[T](x: => T): Future[T] = Future(x)

  def loop(block: => Unit): Unit = {
    while (true) {
      block
    }
  }

  def forEach(f: Int => Unit): Unit = {
    while (true) {
      f(E.pullNext())
    }
  }
}
