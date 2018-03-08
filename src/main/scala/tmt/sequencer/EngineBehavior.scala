package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._
import tmt.sequencer.models.EngineMsg.{ControlCommand, SequencerCommand, SequencerEvent}

import scala.concurrent.Future
import scala.util.{Failure, Success}

class EngineBehavior(script: Script, sequencerRef: ActorRef[SequencerMsg], ctx: ActorContext[EngineMsg])
    extends MutableBehavior[EngineMsg] {

  import ctx.executionContext

  sequencerRef ! GetNext(ctx.self)

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case SequencerCommand(step: Step) =>
        def run(): CommandResult = step.id match {
          case x if x.value.startsWith("setup-")   => script.onSetup(step.command)
          case x if x.value.startsWith("observe-") => script.onObserve(step.command)
          case x                                   => CommandResult.Failed("unknown command")
        }
        Future(concurrent.blocking(run())).onComplete {
          case Success(value) =>
            sequencerRef ! UpdateStatus(step.id, StepStatus.Finished(value))
            sequencerRef ! GetNext(ctx.self)
          case Failure(ex) =>
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

object EngineBehavior {
  def behavior(script: Script, sequencerRef: ActorRef[SequencerMsg]): Behavior[EngineMsg] = {
    Behaviors.mutable(ctx => new EngineBehavior(script, sequencerRef, ctx))
  }
}
