package tmt.sequencer

import akka.actor.Scheduler
import akka.typed.scaladsl.AskPattern._
import akka.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.EngineBehaviour.{Command, Pull, Push}

import scala.concurrent.Await
import scala.concurrent.duration.DurationLong

class Engine(engine: ActorRef[Command], system: ActorSystem[_]) {
  private implicit val timeout: Timeout = Timeout(1.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def pullNext(): Int = Await.result(engine ? Pull, timeout.duration).x
  def push(x: Int): Unit = engine ! Push(x)
}
