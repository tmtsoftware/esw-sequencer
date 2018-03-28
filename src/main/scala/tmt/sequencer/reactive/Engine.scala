package tmt.sequencer.reactive

import java.util.concurrent.Executors

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.Script
import tmt.sequencer.models.EngineMsg.{ControlCommand, SequencerEvent}
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, Future}

class Engine(script: Script, sequencerRef: ActorRef[SequencerMsg], system: ActorSystem[_]) {
  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  private implicit val timeout: Timeout     = Timeout(5.days)
  private implicit val scheduler: Scheduler = system.scheduler

  var currentStep: Step = _

  (sequencerRef ? GetNext).foreach(x => loop(x.step))

  private def loop(step: Step): Unit = {
    currentStep = step
    step.command.name match {
      case x if x.startsWith("setup-") =>
        script.onSetup(step.command).foreach { commandResult =>
          sequencerRef ! UpdateStatus(currentStep.id, StepStatus.Finished(commandResult))
          (sequencerRef ? GetNext).foreach(x => loop(x.step))
        }
      case x =>
    }
  }

  def control(controlCommand: ControlCommand): Future[Unit] = controlCommand.name match {
    case "shutdown" => script.onShutdown()
    case _          => Future.unit
  }

  def event(sequencerEvent: SequencerEvent): Future[Unit] = script.onEvent(sequencerEvent)
}
