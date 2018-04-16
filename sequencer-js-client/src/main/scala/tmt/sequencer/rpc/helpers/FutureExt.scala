package tmt.sequencer.rpc.helpers

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object FutureExt {
  import scala.concurrent.ExecutionContext.Implicits.global
  implicit class RichFuture[T](val f: Future[T]) extends AnyVal {
    def get: T        = Await.result(f, Duration.Inf)
    def print(): Unit = f.onComplete(println)
  }
}
