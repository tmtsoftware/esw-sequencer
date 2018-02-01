package example

import akka.actor.Scheduler
import akka.typed.ActorRef
import akka.typed.scaladsl.ActorContext
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import example.Sequencer.{Command, Pull, Value}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationLong

class ScriptRunner(sequencer: ActorRef[Command], ctx: ActorContext[_]) {
  implicit val timeout: Timeout = Timeout(1.hour)
  implicit val scheduler: Scheduler = ctx.system.scheduler

  import ctx.executionContext

  def run(): Unit = Future {
    while (true) {
      new ScriptLike().run(pullNext().x)
    }
  }

  def pullNext(): Value = Await.result(sequencer ? Pull, timeout.duration)
}

class ScriptLike {
  val dsl: Dsl = Dsl.build()
  import dsl._
  def run(x: Int): Unit = {
    if (x < 2) {
      println((x, "double", double(x)))
    }
    else if (x < 4) {
      println((x, "square", square(x)))
    }
    else {
      println((x, "sum", sum(doubleAsync(x - 4), squareAsync(4))))
    }
  }
}
