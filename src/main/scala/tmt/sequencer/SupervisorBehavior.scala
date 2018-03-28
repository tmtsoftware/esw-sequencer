package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}
import tmt.sequencer.reactive.EngineFuture

class SupervisorBehavior(script: Script,
                         sequencerRef: ActorRef[SequencerMsg],
                         engineFuture: EngineFuture,
                         ctx: ActorContext[SupervisorMsg])
    extends MutableBehavior[SupervisorMsg] {

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand =>
        engineFuture.control(msg)
      case msg: ExternalSequencerMsg =>
        sequencerRef ! msg
      case _ =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(script: Script, sequencerRef: ActorRef[SequencerMsg], engineFuture: EngineFuture): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(script, sequencerRef, engineFuture, ctx))
  }
}
