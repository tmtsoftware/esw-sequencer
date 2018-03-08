package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg._
import tmt.sequencer.models.ScriptRunnerMsg.SequencerCommand
import tmt.sequencer.models.{EngineMsg, Sequence}

class EngineBehaviour(ctx: ActorContext[EngineMsg]) extends MutableBehavior[EngineMsg] {

  var refOpt: Option[ActorRef[SequencerCommand]] = None
  var sequence: Sequence                         = Sequence.empty

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case GetSequence                      => sequence
      case HasNext(replyTo)                 => replyTo ! sequence.hasNext
      case Pull(replyTo)                    => sendNext(replyTo)
      case UpdateStatus(stepId, stepStatus) => sequence = sequence.updateStatus(stepId, stepStatus)
      case Push(commands)                   => sequence = sequence.append(commands)
      case Pause                            => sequence = sequence.pause
      case Resume                           => sequence = sequence.resume
      case Reset                            => sequence = sequence.reset
      case Replace(stepId, commands)        => sequence = sequence.replace(stepId, commands)
      case Prepend(commands)                => sequence = sequence.prepend(commands)
      case Delete(ids)                      => sequence = sequence.delete(ids.toSet)
      case InsertAfter(id, commands)        => sequence = sequence.insertAfter(id, commands)
      case AddBreakpoints(ids)              => sequence = sequence.addBreakpoints(ids)
      case RemoveBreakpoints(ids)           => sequence = sequence.removeBreakpoints(ids)
    }
    trySend()
    this
  }

  def sendNext(replyTo: ActorRef[SequencerCommand]): Unit = {
    if (sequence.hasNext) {
      replyTo ! SequencerCommand(sequence.next.get)
    } else {
      refOpt = Some(replyTo)
    }
  }

  def trySend(): Unit = {
    for {
      ref <- refOpt
      if !sequence.hasNext
      step <- sequence.next
    } {
      ref ! SequencerCommand(step)
      refOpt = None
    }
  }
}

object EngineBehaviour {
  def behavior: Behavior[EngineMsg] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
