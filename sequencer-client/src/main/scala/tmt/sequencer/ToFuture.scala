package tmt.sequencer

import scala.concurrent.Future
import scala.language.higherKinds

trait ToFuture[R[_]] {
  def convert[A](x: R[A]): Future[A]
}

object ToFuture {
  implicit class RichR[R[_]: ToFuture, A](x: R[A]) {
    def asFuture: Future[A] = ToFuture[R].convert[A](x)
  }

  def apply[R[_]](implicit x: ToFuture[R]): ToFuture[R] = x

  implicit val idToFuture: ToFuture[Future] = new ToFuture[Future] {
    override def convert[A](x: Future[A]): Future[A] = x
  }
}
