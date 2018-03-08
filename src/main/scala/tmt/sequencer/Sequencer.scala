package tmt.sequencer

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.{Command, SequencerMsg, Step}

import scala.concurrent.duration.DurationLong

class Sequencer(sequencer: ActorRef[SequencerMsg], system: ActorSystem[_]) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def pullNext(): Step                       = (sequencer ? Pull).await.step
  def pushAll(commands: List[Command]): Unit = sequencer ! Push(commands)
  def hasNext: Boolean                       = (sequencer ? HasNext).await
  def pause(): Unit                          = sequencer ! Pause
  def resume(): Unit                         = sequencer ! Resume
  def reset(): Unit                          = sequencer ! Reset
}
