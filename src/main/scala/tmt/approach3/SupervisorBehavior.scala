package tmt.approach3

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.approach3.EngineMsg.Push
import tmt.approach3.ScriptRunnerMsg.ControlCommand

class SupervisorBehavior(script: Script, engineRef: ActorRef[EngineMsg], ctx: ActorContext[SupervisorMsg])
    extends MutableBehavior[SupervisorMsg] {

  private val scriptRunnerRef: ActorRef[ScriptRunnerMsg] =
    ctx.spawn(ScriptRunnerBehavior.behavior(script, engineRef), "scriptRunner")

  override def onMessage(msg: SupervisorMsg): Behavior[SupervisorMsg] = {
    msg match {
      case msg: ControlCommand => scriptRunnerRef ! msg
      case msg: Push           => engineRef ! msg
      case _                   =>
    }
    this
  }
}

object SupervisorBehavior {
  def behavior(script: Script, engineRef: ActorRef[EngineMsg]): Behavior[SupervisorMsg] = {
    Behaviors.mutable(ctx => new SupervisorBehavior(script, engineRef, ctx))
  }
}
