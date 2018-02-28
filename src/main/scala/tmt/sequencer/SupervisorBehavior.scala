package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.EngineMsg.ExternalEngineMsg
import tmt.sequencer.ScriptRunnerMsg.ControlCommand

class SupervisorBehavior(script: Script, engineRef: ActorRef[EngineMsg], ctx: ActorContext[SupervisorMsg])
    extends MutableBehavior[SupervisorMsg] {

  private val scriptRunnerRef: ActorRef[ScriptRunnerMsg] =
    ctx.spawn(ScriptRunnerBehavior.behavior(script, engineRef), "scriptRunner")

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand    => scriptRunnerRef ! msg
      case msg: ExternalEngineMsg => engineRef ! msg
      case _                      =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(script: Script, engineRef: ActorRef[EngineMsg]): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(script, engineRef, ctx))
  }
}
