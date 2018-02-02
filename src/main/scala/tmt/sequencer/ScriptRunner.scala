package tmt.sequencer

import akka.actor.Scheduler
import akka.typed.scaladsl.AskPattern._
import akka.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.Engine.{Command, Pull}

import scala.concurrent.duration.DurationLong
import scala.concurrent.{Await, Future}

class ScriptRunner(script: Script, engineAdapter: EngineAdapter, system: ActorSystem[_]) {
  import system.executionContext

  def run(): Unit = Future {
    while (true) {
      script.run(engineAdapter.pullNext())
    }
  }

}

class EngineAdapter(engine: ActorRef[Command], system: ActorSystem[_]) {
  private implicit val timeout: Timeout = Timeout(1.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  def pullNext(): Int = Await.result(engine ? Pull, timeout.duration).x
}
