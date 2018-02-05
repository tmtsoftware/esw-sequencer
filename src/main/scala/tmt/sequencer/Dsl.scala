package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.LocationService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Dsl(locationService: LocationService) {
  def square(x: Int): Int = x * x
  def double(x: Int): Int = x + x

  def dummy(): Unit = println(s"location service says ${locationService.m}")

  def par[T](fs: (() => T)*): Seq[T] = {
    Future.traverse(fs)(f => Future(f())).await
  }
}
