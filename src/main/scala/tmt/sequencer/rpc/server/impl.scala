package tmt.sequencer.rpc.server

import tmt.sequencer.rpc.{Advanced, Basic}

import scala.concurrent.Future

object BasicImpl extends Basic {
  def increment(a: Int): Future[Int] = Future.successful(a + 1)
}

object AdvancedImpl extends Advanced {
  def square(a: Int): Future[Int] = Future.successful(a * a)
}
