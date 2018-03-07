package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.Pull
import tmt.sequencer.models.{EngineMsg, ScriptRunnerMsg}
import tmt.sequencer.models.ScriptRunnerMsg.{ControlCommand, SequencerCommand, SequencerEvent}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ScriptRunnerBehavior(script: Script, engineRef: ActorRef[EngineMsg], ctx: ActorContext[ScriptRunnerMsg])
    extends MutableBehavior[ScriptRunnerMsg] {

  import ctx.executionContext

  engineRef ! Pull(ctx.self)

  override def onMessage(msg: ScriptRunnerMsg): Behavior[ScriptRunnerMsg] = {
    msg match {
      case SequencerCommand(command) =>
        def run(): Unit = command.id match {
          case x if x.value.startsWith("setup-")   => script.onSetup(command)
          case x if x.value.startsWith("observe-") => script.onObserve(command)
          case x                                   => println("unknown command")
        }
        Future(concurrent.blocking(run())).onComplete {
          case Success(value) => engineRef ! Pull(ctx.self)
          case Failure(ex)    =>
        }
      case ControlCommand("shutdown") =>
        Future(concurrent.blocking(script.onShutdown())).onComplete {
          case Success(value) =>
          case Failure(ex)    =>
        }
      case msg: SequencerEvent =>
        Future(concurrent.blocking(script.onEvent(msg))).onComplete {
          case Success(value) =>
          case Failure(ex)    =>
        }
      case _ => println(s"unknown sequencer msg=$msg")
    }
    this
  }
}

object ScriptRunnerBehavior {
  def behavior(script: Script, engineRef: ActorRef[EngineMsg]): Behavior[ScriptRunnerMsg] = {
    Behaviors.mutable(ctx => new ScriptRunnerBehavior(script, engineRef, ctx))
  }
}
