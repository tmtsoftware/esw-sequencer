package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg._
import tmt.sequencer.models.ScriptRunnerMsg.SequencerCommand
import tmt.sequencer.models.{EngineMsg, StepStore}

class EngineBehaviour(ctx: ActorContext[EngineMsg]) extends MutableBehavior[EngineMsg] {

  var ref: Option[ActorRef[SequencerCommand]] = None
  var stepStore: StepStore                    = StepStore.empty

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case Push(commands) =>
        stepStore = stepStore.append(commands)
        if (stepStore.hasNext) {
          ref.foreach(x => stepStore.next.foreach(y => x ! SequencerCommand(y)))
          ref = None
        }
      case Pull(replyTo) if stepStore.hasNext => replyTo ! SequencerCommand(stepStore.next.get)
      case Pull(replyTo)                      => ref = Some(replyTo)
      case HasNext(replyTo)                   => replyTo ! stepStore.hasNext
      case Pause                              => stepStore = stepStore.pause()
      case Resume =>
        stepStore = stepStore.resume()
        ref.foreach(x => ctx.self ! Pull(x))
        ref = None
      case Reset => stepStore = stepStore.reset
      case UpdateStepStatusAndPullNext(stepId, stepStatus, replyTo) =>
        stepStore = stepStore.updateStatus(stepId, stepStatus)
        ctx.self ! Pull(replyTo)
      case Replace(stepId, commands) => stepStore = stepStore.replace(stepId, commands)
      case Prepend(commands)         => stepStore = stepStore.prepend(commands)
      case Append(commands)          => stepStore = stepStore.append(commands)
      case Delete(ids)               => stepStore = stepStore.delete(ids)
      case InsertAfter(id, commands) => stepStore = stepStore.insertAfter(id, commands)
      case AddBreakpoints(ids)       => stepStore = stepStore.addBreakpoints(ids)
      case RemoveBreakpoints(ids)    => stepStore = stepStore.removeBreakpoints(ids)
    }
    this
  }
}

object EngineBehaviour {
  def behavior: Behavior[EngineMsg] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
