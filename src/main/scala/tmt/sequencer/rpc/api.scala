package tmt.sequencer.rpc

import scala.concurrent.Future

trait Basic {
  def increment(a: Int): Future[Int]
}

trait Advanced {
  def square(a: Int): Future[Int]
}
