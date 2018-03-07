package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg._
import tmt.sequencer.models.ScriptRunnerMsg.SequencerCommand
import tmt.sequencer.models.{EngineMsg, StepStore}

class EngineBehaviour(ctx: ActorContext[EngineMsg]) extends MutableBehavior[EngineMsg] {

  var refOpt: Option[ActorRef[SequencerCommand]] = None
  var stepStore: StepStore                       = StepStore.empty

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case HasNext(replyTo)                 => replyTo ! stepStore.hasNext
      case Pull(replyTo)                    => sendNext(replyTo)
      case UpdateStatus(stepId, stepStatus) => stepStore = stepStore.updateStatus(stepId, stepStatus)
      case Push(commands)                   => stepStore = stepStore.append(commands)
      case Pause                            => stepStore = stepStore.pause
      case Resume                           => stepStore = stepStore.resume
      case Reset                            => stepStore = stepStore.reset
      case Replace(stepId, commands)        => stepStore = stepStore.replace(stepId, commands)
      case Prepend(commands)                => stepStore = stepStore.prepend(commands)
      case Delete(ids)                      => stepStore = stepStore.delete(ids.toSet)
      case InsertAfter(id, commands)        => stepStore = stepStore.insertAfter(id, commands)
      case AddBreakpoints(ids)              => stepStore = stepStore.addBreakpoints(ids)
      case RemoveBreakpoints(ids)           => stepStore = stepStore.removeBreakpoints(ids)
    }
    trySend()
    this
  }

  def sendNext(replyTo: ActorRef[SequencerCommand]): Unit = {
    if (stepStore.hasNext) {
      replyTo ! SequencerCommand(stepStore.next.get)
    } else {
      refOpt = Some(replyTo)
    }
  }

  def trySend(): Unit = {
    for {
      ref <- refOpt
      if !stepStore.hasNext
      step <- stepStore.next
    } {
      ref ! SequencerCommand(step)
      refOpt = None
    }
  }
}

object EngineBehaviour {
  def behavior: Behavior[EngineMsg] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
