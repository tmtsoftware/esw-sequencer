package tmt.sequencer

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}

import scala.concurrent.duration.DurationDouble
import rx._
import Ctx.Owner.Unsafe._
import tmt.sequencer.FutureExt.RichFuture

import scala.concurrent.Future

class RxDemo extends FunSuite with BeforeAndAfterAll {

  private implicit val actorSystem: ActorSystem        = ActorSystem("test")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  import actorSystem.dispatcher
  private implicit val scheduler: Scheduler = actorSystem.scheduler
  private implicit val timeout: Timeout     = Timeout(5.seconds)

  override protected def afterAll(): Unit = {}

  test("rx") {
    var x     = 0
    val delta = Var(new Delta(0))

    println(x)

    val dd = Rx {
      x += delta().value
    }

    Future
      .sequence(
        List(
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 10000).runForeach(x => delta() = new Delta(-1)),
        )
      )
      .get

    println(x)

  }

  test("rx-atomic") {
    var x = 0

    var wasIncr  = false
    var wasIncr2 = false

    val delta  = Var(new Delta(0))
    val delta2 = Var(new Delta(0))

    println(x)

    Rx {
      Rx {
        if (wasIncr) {
          x -= delta().value
          wasIncr = false
        } else {
          x += delta().value
          wasIncr = true
        }
      }

      Rx {
        if (wasIncr2) {
          x -= delta2().value
          wasIncr2 = false
        } else {
          x += delta2().value
          wasIncr2 = true
        }
      }
    }

    Future
      .sequence(
        List(
          Source(1 to 1000).runForeach(x => delta() = new Delta(1)),
          Source(1 to 1000).runForeach(x => delta2() = new Delta(2)),
        )
      )
      .get

    println(x)

  }

  class Delta(val value: Int)
}
