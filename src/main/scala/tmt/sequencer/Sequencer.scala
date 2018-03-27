package tmt.sequencer

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models._

import scala.concurrent.duration.DurationLong

class Sequencer(sequencer: ActorRef[SequencerMsg], system: ActorSystem[_]) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def next: Step                                         = (sequencer ? GetNext).get.step
  def addAll(commands: List[Command]): Unit              = sequencer ! Add(commands)
  def hasNext: Boolean                                   = (sequencer ? HasNext).get
  def pause(): Unit                                      = sequencer ! Pause
  def resume(): Unit                                     = sequencer ! Resume
  def reset(): Unit                                      = sequencer ! Reset
  def sequence: Sequence                                 = (sequencer ? GetSequence).get
  def delete(ids: List[Id]): Unit                        = sequencer ! Delete(ids)
  def addBreakpoints(ids: List[Id]): Unit                = sequencer ! AddBreakpoints(ids)
  def removeBreakpoints(ids: List[Id]): Unit             = sequencer ! RemoveBreakpoints(ids)
  def insertAfter(id: Id, commands: List[Command]): Unit = sequencer ! InsertAfter(id, commands)
  def prepend(commands: List[Command]): Unit             = sequencer ! Prepend(commands)
  def replace(id: Id, commands: List[Command]): Unit     = sequencer ! Replace(id, commands)
}
