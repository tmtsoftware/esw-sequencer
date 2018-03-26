package tmt.sequencer.reactive

import akka.actor.Scheduler
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.util.Timeout
import reactify.{Channel, Var}
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.Script
import tmt.sequencer.models.EngineMsg.{ControlCommand, SequencerEvent}
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationDouble

class Engine(script: Script, sequencerRef: ActorRef[SequencerMsg], system: ActorSystem[_]) {
  private implicit val executionContext: ExecutionContextExecutor = system.executionContext
  private implicit val timeout: Timeout                           = Timeout(5.days)
  private implicit val scheduler: Scheduler                       = system.scheduler

  val currentStep: Var[Step] = Var(null)

  private val stepCh: Channel[Step]      = Channel[Step] //(sequencerRef ? GetNext).asChannel.map(_.step)
  val resultCh: Channel[CommandResult]   = Channel[CommandResult]
  val controlCh: Channel[ControlCommand] = Channel[ControlCommand]
  val eventCh: Channel[SequencerEvent]   = Channel[SequencerEvent]

  stepCh.attach { step =>
    currentStep := step
    step.command.name match {
      case x if x.startsWith("setup-") => script.onSetup(step.command)
      case x                           =>
    }
  }

  resultCh.attach { commandResult =>
    sequencerRef ! UpdateStatus(currentStep.id, StepStatus.Finished(commandResult))
    (sequencerRef ? GetNext).asChannel.attach(x => stepCh := x.step)
  }

  controlCh.attach { control =>
    control.name match {
      case "shutdown" => script.onShutdown()
      case _          =>
    }
  }

  eventCh.attach(script.onEvent)
}
