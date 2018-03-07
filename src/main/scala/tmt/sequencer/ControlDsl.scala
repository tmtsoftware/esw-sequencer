package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.CommandResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions

trait ControlDsl {
  implicit def toFuture(x: => CommandResult): Future[CommandResult] = Future(x)

  def par(fs: Future[CommandResult]*): Seq[CommandResult] = Future.sequence(fs.toList).await

  implicit class RichCommandResponse(commandResponse: => CommandResult) {
    def async: Future[CommandResult] = Future(commandResponse)
  }
}
