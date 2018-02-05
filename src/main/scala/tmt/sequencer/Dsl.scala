package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.Command

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

object Dsl {
  private[tmt] val wiring = new Wiring

  lazy val CS: CommandService = wiring.commandService
  lazy val E: Engine = wiring.engine

  implicit def toFuture[T](x: => T): Future[T] = Future(x)

  def loop(block: => Unit): Unit = {
    while (true) {
      block
    }
  }

  def forEach(f: Command => Unit): Unit = {
    loop(f(E.pullNext()))
  }

  def par[T](fs: Future[T]*): Seq[T] = Future.sequence(fs.toList).await
}
