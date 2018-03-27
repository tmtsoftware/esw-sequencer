package tmt.sequencer

import java.util.concurrent.Executors

import reactify.Var
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.CommandResult

import scala.annotation.compileTimeOnly
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.language.experimental.macros
import scala.language.implicitConversions
import scala.util.{Failure, Success}
import scala.async.internal

trait ControlDsl {
  implicit def toFuture(x: => CommandResult): Future[CommandResult] = Future(x)

  def par(fs: Future[CommandResult]*): Future[List[CommandResult]] = Future.sequence(fs.toList)

  implicit class RichCommandResponse(commandResponse: => CommandResult) {
    def async: Future[CommandResult] = Future(commandResponse)
  }

  implicit class RichVar(x: Var[CommandResult]) {
    def :=(f: Future[CommandResult]): Unit = f.onComplete {
      case Success(t)  => x := t
      case Failure(ex) => x := CommandResult.Failed(ex.getMessage)
    }
  }

  implicit val ec: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  def async[T](body: => T)(implicit execContext: ExecutionContext): Future[T] = macro internal.ScalaConcurrentAsync.asyncImpl[T]
  @compileTimeOnly("`await` must be enclosed in an `spawn` block")
  def await[T](awaitable: Future[T]): T =
    ??? // No implementation here, as calls to this are translated to `onComplete` by the macro.
}
