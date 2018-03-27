package tmt.sequencer

import org.tmt.macros.AsyncMacros

import scala.concurrent.Future
import scala.language.experimental.macros

object Fiber {
  def spawn[T](body: => T): Future[T] = macro AsyncMacros.spawn[T]

  implicit class RichF[T](t: concurrent.Future[T]) {
    final def get: T = macro AsyncMacros.await
  }
}
