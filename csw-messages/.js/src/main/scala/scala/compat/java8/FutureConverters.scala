package scala.compat.java8

import java.util.concurrent.CompletionStage

import scala.concurrent.Future

object FutureConverters {
  implicit class FutureOps[T](val underlying: Future[T]) extends AnyVal {
    def toJava: CompletionStage[T] = ???
  }
}
