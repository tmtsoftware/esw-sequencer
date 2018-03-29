package tmt.sequencer.core

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.dsl.Script
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}

class SupervisorBehavior(script: Script,
                         sequencerRef: ActorRef[SequencerMsg],
                         engineFuture: Engine,
                         ctx: ActorContext[SupervisorMsg])
    extends MutableBehavior[SupervisorMsg] {

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand       => engineFuture.control(msg)
      case msg: ExternalSequencerMsg => sequencerRef ! msg
      case _                         =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(script: Script, sequencerRef: ActorRef[SequencerMsg], engineFuture: Engine): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(script, sequencerRef, engineFuture, ctx))
  }
}
