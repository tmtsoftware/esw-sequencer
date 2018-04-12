package tmt.sequencer.rpc_spike

import scala.concurrent.Future
import scala.language.higherKinds

trait Basic {
  def increment(a: Int): Future[Int]
}

trait Advanced {
  def square(a: Int): Future[Int]
}

trait Streaming[Result[_]] {
  def from(a: Int): Result[String]
}
