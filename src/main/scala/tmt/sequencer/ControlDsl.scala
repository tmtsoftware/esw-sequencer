package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.{Command, CommandResponse}

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

trait ControlDsl {
  def engine: Engine

  implicit def toFuture(x: => CommandResponse): Future[CommandResponse] = Future(x)

  def forEach(f: Command => Unit): Unit = {
    loop(f(engine.pullNext()))
  }

  def loop(block: => Unit): Unit = Future {
    while (true) {
      block
    }
  }

  def par[T](fs: Future[T]*): Seq[T] = Future.sequence(fs.toList).await
}
