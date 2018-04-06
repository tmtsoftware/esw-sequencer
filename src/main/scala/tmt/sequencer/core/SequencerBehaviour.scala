package tmt.sequencer.core

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.SequencerMsg._
import tmt.sequencer.models.{Sequence, SequencerMsg, Step, StepStatus}

import scala.util.{Failure, Try}

object SequencerBehaviour {
  def behavior: Behavior[SequencerMsg] = Behaviors.setup { _ =>
    var stepRefOpt: Option[ActorRef[Step]]              = None
    var sequence: Sequence                              = Sequence.empty
    var sequenceRefOpt: Option[ActorRef[Try[Sequence]]] = None

    def sendNext(replyTo: ActorRef[Step]): Unit = sequence.next match {
      case Some(step) => setInFlight(replyTo, step)
      case None       => stepRefOpt = Some(replyTo)
    }

    def trySend(): Unit = {
      for {
        ref  <- stepRefOpt
        step <- sequence.next
      } {
        setInFlight(ref, step)
        stepRefOpt = None
      }
    }

    def setInFlight(replyTo: ActorRef[Step], step: Step): Unit = {
      val inFlightStep = step.withStatus(StepStatus.InFlight)
      sequence = sequence.updateStep(inFlightStep)
      replyTo ! inFlightStep
    }

    Behaviors.immutable { (_, msg) =>
      if (sequence.isFinished) {
        msg match {
          case ProcessSequence(_sequence, replyTo) =>
            if (_sequence == Sequence.empty) {
              replyTo ! Failure(new RuntimeException("empty sequence can not be processed"))
            } else {
              sequence = _sequence
              stepRefOpt = None
              sequenceRefOpt = Some(replyTo)
            }
          case GetSequence(replyTo) => replyTo ! sequence
          case x                    => println(s"command=$x can not be applied on a finished sequence")
        }
      } else {
        msg match {
          case ProcessSequence(_sequence, replyTo) =>
            replyTo ! Failure(new RuntimeException("previous sequence has not finished yet"))
          case GetSequence(replyTo)      => replyTo ! sequence
          case GetNext(replyTo)          => sendNext(replyTo)
          case MaybeNext(replyTo)        => replyTo ! sequence.next
          case Update(step)              => sequence = sequence.updateStep(step)
          case Add(commands)             => sequence = sequence.append(commands)
          case Pause                     => sequence = sequence.pause
          case Resume                    => sequence = sequence.resume
          case DiscardPending            => sequence = sequence.discardPending
          case Replace(stepId, commands) => sequence = sequence.replace(stepId, commands)
          case Prepend(commands)         => sequence = sequence.prepend(commands)
          case Delete(ids)               => sequence = sequence.delete(ids.toSet)
          case InsertAfter(id, commands) => sequence = sequence.insertAfter(id, commands)
          case AddBreakpoints(ids)       => sequence = sequence.addBreakpoints(ids)
          case RemoveBreakpoints(ids)    => sequence = sequence.removeBreakpoints(ids)
        }
      }
      trySend()
      Behaviors.same
    }
  }
}
