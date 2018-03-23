package tmt.sequencer

import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import reactify._
import tmt.sequencer.FutureActor.{ActorMsg, FutureMsg, GetX}
import tmt.sequencer.FutureExt.RichFuture

import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class RxDemo extends FunSuite with BeforeAndAfterAll {

  private implicit val actorSystem: ActorSystem        = ActorSystem("test")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  import actorSystem.dispatcher
  private implicit val scheduler: Scheduler = actorSystem.scheduler
  private implicit val timeout: Timeout     = Timeout(5.seconds)

  override protected def afterAll(): Unit = {}

  test("plain") {
    @volatile
    var x = 0

    println(x)

    val dd = List(
      Source(1 to 10000).runForeach(_ => x = x + 1),
      Source(1 to 10000).runForeach(_ => x = x - 1),
    )

    Future.sequence(dd).await

    println(x)

  }

  test("reactify") {
    val x = Var(0)

//    z.attach(println)

    println(x())

    val dd = List(
      Source(1 to 10000).runForeach(_ => x := x() + 1),
      Source(1 to 10000).runForeach(_ => x := x() - 1),
      Source(1 to 10000).runForeach(_ => x := x() + 1),
      Source(1 to 10000).runForeach(_ => x := x() - 1),
      Source(1 to 10000).runForeach(_ => x := x() + 1),
      Source(1 to 10000).runForeach(_ => x := x() - 1),
      Source(1 to 10000).runForeach(_ => x := x() + 1),
      Source(1 to 10000).runForeach(_ => x := x() - 1),
    )

    Future.sequence(dd).await

    println(x())

  }

  test("future-actor") {
    val actorRef = actorSystem.spawnAnonymous(FutureActor.behavior)

    println((actorRef ? GetX).await)

    val dd = List(
      Source(1 to 10000).runForeach(_ => actorRef ! ActorMsg),
      Source(1 to 10000).runForeach(_ => actorRef ! FutureMsg),
    )

    Future.sequence(dd).await

    Thread.sleep(2000)

    println((actorRef ? GetX).await)
  }

  test("atomic-future-actor") {
    val actorRef = actorSystem.spawnAnonymous(AtomicFutureActor.behavior)

    println((actorRef ? AtomicFutureActor.GetX).await)

    val dd = List(
      Source(1 to 10000).runForeach(_ => actorRef ! AtomicFutureActor.ActorMsg),
      Source(1 to 10000).runForeach(_ => actorRef ! AtomicFutureActor.FutureMsg),
    )

    Future.sequence(dd).await

    Thread.sleep(2000)

    println((actorRef ? AtomicFutureActor.GetX).await)
  }

  test("reactify-atomic") {
    val x                            = Var(0)
    val wasIncremented: Var[Boolean] = Var(false)

    //    z.attach(println)

    println(x())

    def f = Source(1 to 10000).runForeach { _ =>
      if (wasIncremented) {
        x := x() - 1
        wasIncremented := false
      } else {
        x := x() + 1
        wasIncremented := true
      }
    }

    Future.sequence(List.fill(10)(f)).await

    println(x())

  }

  test("observe") {
    val x = Var(0)
    x.attach { d =>
      println(d)
      x := 10
    }

    println(x())
    x := 100
    println(x())
  }

}
