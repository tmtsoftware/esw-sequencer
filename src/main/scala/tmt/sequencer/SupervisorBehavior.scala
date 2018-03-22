package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.ExternalSequencerMsg
import tmt.sequencer.models.{EngineMsg, SequencerMsg, SupervisorMsg}

class SupervisorBehavior(script: Script, ctx: ActorContext[SupervisorMsg]) extends MutableBehavior[SupervisorMsg] {

  private val sequencerRef: ActorRef[SequencerMsg] =
    ctx.spawn(SequencerBehaviour.behavior, "sequencer")

  private val engineRef: ActorRef[EngineMsg] =
    ctx.spawn(EngineBehavior.behavior(script, sequencerRef), "engine")

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand       => engineRef ! msg
      case msg: ExternalSequencerMsg => sequencerRef ! msg
      case _                         =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(script: Script): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(script, ctx))
  }
}
