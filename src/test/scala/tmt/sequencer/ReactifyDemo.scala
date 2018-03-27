package tmt.sequencer

import akka.actor.{ActorSystem, Scheduler}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import org.scalatest.{BeforeAndAfterAll, FunSuite}
import reactify._
import tmt.sequencer.FutureExt.RichFuture

import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class ReactifyDemo extends FunSuite with BeforeAndAfterAll {

  private implicit val actorSystem: ActorSystem        = ActorSystem("test")
  private implicit val materializer: ActorMaterializer = ActorMaterializer()
  import actorSystem.dispatcher
  private implicit val scheduler: Scheduler = actorSystem.scheduler
  private implicit val timeout: Timeout     = Timeout(5.seconds)

  override protected def afterAll(): Unit = {}

  test("reactify") {
    val x     = Var(0)
    val delta = Var(new Delta(0))

    println(x)

    delta.attach { d =>
      x := x + d.value
    }

    Future
      .sequence(
        List(
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(-1)),
        )
      )
      .get

    println(x)

  }

  test("reactify-atomic") {
    val x = Var(0)

    val wasIncr  = Var(false)
    val wasIncr2 = Var(false)

    val delta  = Var(new Delta(0))
    val delta2 = Var(new Delta(0))

    println(x)

    delta.attach { d =>
      if (wasIncr) {
        x := x - d.value
        wasIncr := false
      } else {
        x := x + d.value
        wasIncr := true
      }
    }

    delta2.attach { d =>
      if (wasIncr2) {
        x := x - d.value
        wasIncr2 := false
      } else {
        x := x + d.value
        wasIncr2 := true
      }
    }

    Future
      .sequence(
        List(
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(-1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(-1)),
        )
      )
      .get

    println(x)

  }

  test("reactify-atomic-merge") {
    val x = Var(0)

    val wasIncr = Var(false)

    val delta  = Var(new Delta(0))
    val delta2 = Var(new Delta(0))

    delta.attach { d =>
      if (wasIncr) {
        x := x - d.value
        wasIncr := false
      } else {
        x := x + d.value
        wasIncr := true
      }
    }

    delta2.attach { d =>
      if (wasIncr) {
        x := x - d.value
        wasIncr := false
      } else {
        x := x + d.value
        wasIncr := true
      }
    }

    println(x)

    Future
      .sequence(
        List(
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta := new Delta(1)),
          Source(1 to 10000).runForeach(x => delta2 := new Delta(1)),
        )
      )
      .get

    println(x)

  }

  test("dependency") {
    val x = Var(0)
    val y = Var(0)

    val z = Val(x() + y())

    z.attach(println)
    x := 10
    y := 10

  }

  test("channels") {
    val x = Var(10)
    val y = Var(0)

    val delta = Channel[Delta]

    delta.attach { d =>
      Future {
        Thread.sleep(1000)
        100
      }.asChannel.attach { result =>
        println(d.value + result)
      }
    }

    delta := new Delta(10)

    Thread.sleep(2000)
  }

  class Delta(val value: Int)
}
