package tmt.sequencer.core

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}

class SupervisorBehavior(sequencerRef: ActorRef[SequencerMsg], engine: Engine, ctx: ActorContext[SupervisorMsg])
    extends MutableBehavior[SupervisorMsg] {

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand       => engine.control(msg)
      case msg: ExternalSequencerMsg => sequencerRef ! msg
      case _                         =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(sequencerRef: ActorRef[SequencerMsg], engine: Engine): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(sequencerRef, engine, ctx))
  }
}
