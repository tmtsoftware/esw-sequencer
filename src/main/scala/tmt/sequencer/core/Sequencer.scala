package tmt.sequencer.core

import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.models._

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try

class Sequencer(sequencer: ActorRef[SequencerMsg], system: ActorSystem) {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  def next: Future[Step]                                 = sequencer ? GetNext
  def maybeNext: Future[Option[Step]]                    = sequencer ? MaybeNext
  def addAll(commands: List[Command]): Unit              = sequencer ! Add(commands)
  def pause(): Unit                                      = sequencer ! Pause
  def resume(): Unit                                     = sequencer ! Resume
  def reset(): Unit                                      = sequencer ! DiscardPending
  def sequence: Future[Sequence]                         = sequencer ? GetSequence
  def delete(ids: List[Id]): Unit                        = sequencer ! Delete(ids)
  def addBreakpoints(ids: List[Id]): Unit                = sequencer ! AddBreakpoints(ids)
  def removeBreakpoints(ids: List[Id]): Unit             = sequencer ! RemoveBreakpoints(ids)
  def insertAfter(id: Id, commands: List[Command]): Unit = sequencer ! InsertAfter(id, commands)
  def prepend(commands: List[Command]): Unit             = sequencer ! Prepend(commands)
  def replace(id: Id, commands: List[Command]): Unit     = sequencer ! Replace(id, commands)

  def processSequence(commands: List[Command]): Future[AggregateResponse] = {
    val dd: Future[Try[AggregateResponse]] = sequencer ? (x => ProcessSequence(commands, x))
    dd.map(_.get)
  }

  private[sequencer] def update(response: AggregateResponse): Unit = sequencer ! Update(response)
}
