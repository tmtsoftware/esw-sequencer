package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Dsl(locationService: LocationService) {
  def square(x: Int): Int = Async.square(x).await
  def double(x: Int): Int = Async.double(x).await

  def dummy(): Unit = println(s"location service says ${locationService.m}")

  object Async {
    def square(x: Int): Future[Int] = Future(x * x)
    def double(x: Int): Future[Int] = Future(x + x)

    def par[T](xs: Future[T]*): Seq[T] = Future.sequence(xs).await
  }
}
