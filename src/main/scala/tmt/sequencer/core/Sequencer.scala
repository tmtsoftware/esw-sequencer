package tmt.sequencer.core

import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.models._

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong

class Sequencer(sequencer: ActorRef[SequencerMsg], system: ActorSystem) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  def next: Future[Step]                                 = (sequencer ? GetNext).map(_.step)
  def addAll(commands: List[Command]): Unit              = sequencer ! Add(commands)
  def hasNext: Future[Boolean]                           = sequencer ? HasNext
  def pause(): Unit                                      = sequencer ! Pause
  def resume(): Unit                                     = sequencer ! Resume
  def reset(): Unit                                      = sequencer ! Reset
  def sequence: Future[Sequence]                         = sequencer ? GetSequence
  def delete(ids: List[Id]): Unit                        = sequencer ! Delete(ids)
  def addBreakpoints(ids: List[Id]): Unit                = sequencer ! AddBreakpoints(ids)
  def removeBreakpoints(ids: List[Id]): Unit             = sequencer ! RemoveBreakpoints(ids)
  def insertAfter(id: Id, commands: List[Command]): Unit = sequencer ! InsertAfter(id, commands)
  def prepend(commands: List[Command]): Unit             = sequencer ! Prepend(commands)
  def replace(id: Id, commands: List[Command]): Unit     = sequencer ! Replace(id, commands)

  private[sequencer] def update(step: Step): Unit = sequencer ! Update(step)
}
