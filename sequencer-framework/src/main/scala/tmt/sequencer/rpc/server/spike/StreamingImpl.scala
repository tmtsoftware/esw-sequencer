package tmt.sequencer.rpc.server.spike

import covenant.core.api.ApiDsl
import monix.reactive.Observable
import tmt.sequencer.api.Streaming

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

object StreamingImpl extends Streaming[StreamingDsl.ApiFunction] {
  import StreamingDsl._
  import scala.concurrent.ExecutionContext.Implicits.global

  def from(a: Int): ApiFunction[String] = Action { state =>
    val iterator = Iterator.from(a).map(x => List(x))
    val obs      = Observable.fromIterator(iterator).delayOnNext(1.second)
    Future.successful(Returns(s"started stream from $a", obs))
  }
}

object StreamingDsl extends ApiDsl[Int, String, List[Int]] {
  override def applyEventsToState(state: List[Int], events: Seq[Int]): List[Int] = state ++ events
  override def unhandledException(t: Throwable): String                          = t.getMessage
}
