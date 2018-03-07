package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.EngineMsg.{updateStepStatusAndPullNext, Pull}
import tmt.sequencer.models._
import tmt.sequencer.models.ScriptRunnerMsg.{ControlCommand, SequencerCommand, SequencerEvent}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ScriptRunnerBehavior(script: Script, engineRef: ActorRef[EngineMsg], ctx: ActorContext[ScriptRunnerMsg])
    extends MutableBehavior[ScriptRunnerMsg] {

  import ctx.executionContext

  engineRef ! Pull(ctx.self)

  override def onMessage(msg: ScriptRunnerMsg): Behavior[ScriptRunnerMsg] = {
    msg match {
      case SequencerCommand(step: Step) =>
        def run(): CommandResult = step.id match {
          case x if x.value.startsWith("setup-")   => script.onSetup(step.command)
          case x if x.value.startsWith("observe-") => script.onObserve(step.command)
          case x                                   => CommandResult.Failed("unknown command")
        }
        Future(concurrent.blocking(run())).onComplete {
          case Success(value) => engineRef ! updateStepStatusAndPullNext(step.id, StepStatus.Finished(value), ctx.self)
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
