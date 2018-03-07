package tmt.sequencer

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.models.EngineMsg._
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.{Command, EngineMsg, Step}

import scala.concurrent.duration.DurationLong

class Engine(engine: ActorRef[EngineMsg], system: ActorSystem[_]) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def pullNext(): Step                       = (engine ? Pull).await.step
  def pushAll(commands: List[Command]): Unit = engine ! Push(commands)
  def hasNext: Boolean                       = (engine ? HasNext).await
  def pause(): Unit                          = engine ! Pause
  def resume(): Unit                         = engine ! Resume
  def reset(): Unit                          = engine ! Reset
}
