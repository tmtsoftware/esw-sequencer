package tmt.sequencer.core

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.SequencerCommand
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.models.{Sequence, SequencerMsg, Step, StepStatus}

object SequencerBehaviour {
  def behavior: Behavior[SequencerMsg] = Behaviors.setup { _ =>
    var refOpt: Option[ActorRef[SequencerCommand]] = None
    var sequence: Sequence                         = Sequence.empty

    def sendNext(replyTo: ActorRef[SequencerCommand]): Unit = {
      if (sequence.hasNext) {
        send(replyTo, sequence.next.get)
      } else {
        refOpt = Some(replyTo)
      }
    }

    def trySend(): Unit = {
      for {
        ref <- refOpt
        if sequence.hasNext
        step <- sequence.next
      } {
        send(ref, step)
        refOpt = None
      }
    }

    def send(replyTo: ActorRef[SequencerCommand], step: Step): Unit = {
      val inFlightStep = step.withStatus(StepStatus.InFlight)
      sequence = sequence.updateStep(inFlightStep)
      replyTo ! SequencerCommand(inFlightStep)
    }

    Behaviors.immutable { (_, msg) =>
      msg match {
        case GetSequence(replyTo)      => replyTo ! sequence
        case HasNext(replyTo)          => replyTo ! sequence.hasNext
        case GetNext(replyTo)          => sendNext(replyTo)
        case Update(step)              => sequence = sequence.updateStep(step)
        case Add(commands)             => sequence = sequence.append(commands)
        case Pause                     => sequence = sequence.pause
        case Resume                    => sequence = sequence.resume
        case Reset                     => sequence = sequence.reset
        case Replace(stepId, commands) => sequence = sequence.replace(stepId, commands)
        case Prepend(commands)         => sequence = sequence.prepend(commands)
        case Delete(ids)               => sequence = sequence.delete(ids.toSet)
        case InsertAfter(id, commands) => sequence = sequence.insertAfter(id, commands)
        case AddBreakpoints(ids)       => sequence = sequence.addBreakpoints(ids)
        case RemoveBreakpoints(ids)    => sequence = sequence.removeBreakpoints(ids)
      }
      trySend()
      Behaviors.same
    }
  }
}
