package example

import akka.actor.Scheduler
import akka.typed.ActorRef
import akka.typed.scaladsl.ActorContext
import akka.typed.scaladsl.AskPattern._
import akka.util.Timeout
import example.Sequencer.{Command, Pull, Value}

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationLong

class ScriptLike(sequencer: ActorRef[Command], ctx: ActorContext[_]) {
  implicit val timeout: Timeout = Timeout(1.hour)
  implicit val scheduler: Scheduler = ctx.system.scheduler

  import ctx.executionContext

  def run(): Unit = Future {
    while (true) {
      run(pullNext().x)
    }
  }

  def run(x: Int): Unit = {
    if (x < 2) {
      println((x, "double", CommandService.double(x)))
    }
    else if (x < 4) {
      println((x, "square", CommandService.square(x)))
    }
    else {
      println((x, "sum", CommandService.sum(CommandService.doubleAsync(x - 4), CommandService.squareAsync(4))))
    }
  }

  def pullNext(): Value = Await.result(sequencer ? Pull, timeout.duration)
}
