package tmt.sequencer.core

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.Behaviors
import tmt.sequencer.ScriptImports.Script
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg

object SupervisorBehavior {
  def behavior(sequencerRef: ActorRef[SequencerMsg], script: Script): Behaviors.Immutable[SupervisorMsg] =
    Behaviors.immutable { (_, msg) =>
      msg match {
        case ControlCommand("shutdown") => script.shutdown()
        case msg: ExternalSequencerMsg  => sequencerRef ! msg
        case _                          =>
      }
      Behaviors.same
    }
}
