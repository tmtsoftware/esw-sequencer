package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.CommandResponse

import scala.concurrent.Future
import scala.language.implicitConversions
import scala.concurrent.ExecutionContext.Implicits.global

object Dsl {
  private[tmt] val wiring = new Wiring
  private[tmt] def init(): Unit = {
    //touch system so that main does not exit
    wiring.system
  }

  lazy val cs: CommandService = wiring.commandService
  lazy val engine: Engine = wiring.engine

  implicit def toFuture(x: => CommandResponse): Future[CommandResponse] = Future(x)

  def forEach(f: Command => Unit): Unit = {
    loop(f(wiring.engine.pullNext()))
  }

  def loop(block: => Unit): Unit = Future {
    while (true) {
      block
    }
  }

  def par[T](fs: Future[T]*): Seq[T] = Future.sequence(fs.toList).await

  val Command = tmt.services.Command
  type Command = tmt.services.Command
}
