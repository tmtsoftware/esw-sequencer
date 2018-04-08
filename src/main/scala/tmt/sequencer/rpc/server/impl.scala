package tmt.sequencer.rpc.server

import covenant.core.api.ApiDsl
import monix.reactive.Observable
import tmt.sequencer.rpc.{Advanced, Basic, Streaming}

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

object BasicImpl extends Basic {
  def increment(a: Int): Future[Int] = Future.successful(a + 1)
}

object AdvancedImpl extends Advanced {
  def square(a: Int): Future[Int] = Future.successful(a * a)
}

object StreamingImpl extends Streaming[StreamingDsl.ApiFunction] {
  import StreamingDsl._
  import monix.execution.Scheduler.Implicits.global

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
