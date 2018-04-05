package tmt.sequencer.core

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import tmt.sequencer.ScriptImports.Script
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg
import tmt.sequencer.models.SupervisorMsg.ControlCommand

object SupervisorBehavior {
  def behavior(sequencerRef: ActorRef[SequencerMsg], script: Script): Behaviors.Immutable[SupervisorMsg] =
    Behaviors.immutable { (ctx, msg) =>
      import ctx.executionContext
      msg match {
        case ControlCommand("shutdown", replyTo) => script.shutdown().onComplete(x => replyTo ! x)
        case msg: ExternalSequencerMsg           => sequencerRef ! msg
        case _                                   =>
      }
      Behaviors.same
    }
}
