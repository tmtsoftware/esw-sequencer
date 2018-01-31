package example

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object CommandService {
  def square(x: Int): Int = x * x
  def squareAsync(x: Int): Future[Int] = Future(x * x)

  def double(x: Int): Int = x + x
  def doubleAsync(x: Int): Future[Int] = Future(x + x)

  def sum(xs: Future[Int]*): Int = Await.result(Future.sequence(xs), 1.minute).sum
}
