package tmt.sequencer

import java.util.concurrent.Executors

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import tmt.sequencer.FutureExt.RichFuture

import scala.annotation.compileTimeOnly
import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService, Future}

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
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x -= 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x += 1)).runForeach(_ => ()),
          Source(1 to 10000).mapAsync(1)(d => Fiber.async(x -= 1)).runForeach(_ => ()),
        )
      )
      .await

    println(x)

  }

  test("future-atomic") {
    var x = 0

    var wasIncr = false

    println(x)

    def f(d: Int) = Fiber.async {
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

  def async[T](body: => T)(implicit execContext: ExecutionContext): Future[T] = macro internal.ScalaConcurrentAsync.asyncImpl[T]
  @compileTimeOnly("`await` must be enclosed in an `spawn` block")
  def await[T](awaitable: Future[T]): T =
    ??? // No implementation here, as calls to this are translated to `onComplete` by the macro.
}
