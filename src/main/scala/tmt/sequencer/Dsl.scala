package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Dsl(locationService: LocationService) {
  def square(x: Int): Int = x * x
  def double(x: Int): Int = x + x

  def par[T](fs: Future[T]*): Seq[T] = Future.sequence(fs.toList).await

  def dummy(): Unit = println(s"location service says ${locationService.m}")
}
