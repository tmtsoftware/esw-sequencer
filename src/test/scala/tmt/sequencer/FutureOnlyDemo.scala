package tmt.sequencer

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import reactify._
import tmt.sequencer.FutureExt.RichFuture

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}
import scala.concurrent.duration.DurationDouble

class FutureOnlyDemo extends FunSuite with BeforeAndAfterAll {

  private implicit val actorSystem: ActorSystem = ActorSystem("test")
  import actorSystem.dispatcher
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  private implicit val scheduler: Scheduler            = actorSystem.scheduler
  private implicit val timeout: Timeout                = Timeout(5.seconds)

  override protected def afterAll(): Unit = {}

  test("future") {
    var x = 0

    println(x)

    Future
      .sequence(
        List(
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.spawn(x -= 1)).runForeach(_ => ()),
        )
      )
      .await

    println(x)

  }

  test("future-atomic") {
    var x = 0

    var wasIncr = false

    println(x)

    def f(d: Int) = Fiber.spawn {
      if (wasIncr) {
        x = x - d
        wasIncr = false
      } else {
        x = x + d
        wasIncr = true
      }
    }

    Future
      .sequence(
        List(
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(_ => f(1)).runForeach(_ => ()),
        )
      )
      .await

    println(x)

  }

}

object Fiber {
  private implicit val ec: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  def spawn[T](x: => T) = Future(x)
}
