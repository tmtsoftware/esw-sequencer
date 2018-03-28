package tmt.sequencer

import java.util.concurrent.Executors

import org.tmt.macros.AsyncMacros
import tmt.sequencer.models.CommandResult

import scala.language.implicitConversions
import scala.language.experimental.macros
import scala.concurrent.{ExecutionContext, Future}
import scala.annotation.compileTimeOnly
import scala.async.internal

trait ControlDsl {
  implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())

  def par(fs: Future[CommandResult]*): Future[List[CommandResult]] = Future.sequence(fs.toList)

  implicit class RichF[T](t: Future[T]) {
    final def await: T = macro AsyncMacros.await
  }

  def spawn[T](body: => T)(implicit ec: ExecutionContext): Future[T] = macro AsyncMacros.async[T]

}

object ControlDsl extends ControlDsl

object Async {
  def async[T](body: => T)(implicit execContext: ExecutionContext): Future[T] = macro internal.ScalaConcurrentAsync.asyncImpl[T]
  @compileTimeOnly("`await` must be enclosed in an `spawn` block")
  def await[T](awaitable: Future[T]): T =
    ??? // No implementation here, as calls to this are translated to `onComplete` by the macro.
}
