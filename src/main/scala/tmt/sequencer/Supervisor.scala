package tmt.sequencer

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.models._

import scala.concurrent.duration.DurationLong

class Supervisor(supervisor: ActorRef[SupervisorMsg], system: ActorSystem[_]) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler

  def next: Step                                         = (supervisor ? GetNext).await.step
  def addAll(commands: List[Command]): Unit              = supervisor ! Add(commands)
  def hasNext: Boolean                                   = (supervisor ? HasNext).await
  def pause(): Unit                                      = supervisor ! Pause
  def resume(): Unit                                     = supervisor ! Resume
  def reset(): Unit                                      = supervisor ! Reset
  def sequence: Sequence                                 = (supervisor ? GetSequence).await
  def delete(ids: List[Id]): Unit                        = supervisor ! Delete(ids)
  def addBreakpoints(ids: List[Id]): Unit                = supervisor ! AddBreakpoints(ids)
  def removeBreakpoints(ids: List[Id]): Unit             = supervisor ! RemoveBreakpoints(ids)
  def insertAfter(id: Id, commands: List[Command]): Unit = supervisor ! InsertAfter(id, commands)
  def prepend(commands: List[Command]): Unit             = supervisor ! Prepend(commands)
  def replace(id: Id, commands: List[Command]): Unit     = supervisor ! Replace(id, commands)
}
