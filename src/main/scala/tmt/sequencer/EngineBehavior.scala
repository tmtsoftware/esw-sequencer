package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._
import tmt.sequencer.models.EngineMsg._

import scala.concurrent.Future
import scala.util.{Failure, Success}

class EngineBehavior(script: Script, sequencerRef: ActorRef[SequencerMsg], ctx: ActorContext[EngineMsg])
    extends MutableBehavior[EngineMsg] {

  import ctx.executionContext

  var currentStep: Step = null

//  sequencerRef ! GetNext(ctx.self)

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case SequencerCommand(step: Step) =>
        currentStep = step
        step.command.name match {
          case x if x.startsWith("setup-") => script.onSetup(step.command)
          case x                           =>
        }
      case CommandCompletion(commandResult) => script.onCommandCompletion(commandResult)
      case StepCompletion(commandResult) =>
        script.onStepCompletion(commandResult)
        sequencerRef ! UpdateStatus(currentStep.id, StepStatus.Finished(commandResult))
        sequencerRef ! GetNext(ctx.self)
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

object EngineBehavior {
  def behavior(script: Script, sequencerRef: ActorRef[SequencerMsg]): Behavior[EngineMsg] = {
    Behaviors.mutable(ctx => new EngineBehavior(script, sequencerRef, ctx))
  }
}
