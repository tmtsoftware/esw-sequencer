package tmt.sequencer

import reactify.Var
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.CommandResult

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.language.implicitConversions
import scala.util.{Failure, Success}

trait ControlDsl {
  implicit def toFuture(x: => CommandResult): Future[CommandResult] = Future(x)

  def par(fs: Future[CommandResult]*): Seq[CommandResult] = Future.sequence(fs.toList).await

  implicit class RichCommandResponse(commandResponse: => CommandResult) {
    def async: Future[CommandResult] = Future(commandResponse)
  }

  implicit class RichVar(x: Var[CommandResult]) {
    def :=(f: Future[CommandResult]): Unit = f.onComplete {
      case Success(t)  => x := t
      case Failure(ex) => x := CommandResult.Failed(ex.getMessage)
    }
  }
}
