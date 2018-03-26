package tmt.sequencer

import reactify.Channel
import tmt.sequencer.models.CommandResult

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.NonFatal

object FutureExt {
  implicit class RichFuture[T](val f: Future[T]) extends AnyVal {
    def await(duration: Duration): T = Await.result(f, duration)
    def await: T                     = await(Duration.Inf)
    def asChannel(implicit ec: ExecutionContext): Channel[T] = {
      val channel = Channel[T]
      f.map(x => channel := x).recover {
        case NonFatal(ex) => ex.printStackTrace()
      }
      channel
    }

  }

  implicit class CommandResultFuture(val f: Future[CommandResult]) extends AnyVal {
    def resultChannel(implicit ec: ExecutionContext): Channel[CommandResult] =
      f.recover {
        case NonFatal(ex) => CommandResult.Failed(ex.getMessage)
      }.asChannel
  }
}
