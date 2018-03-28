package tmt.sequencer.core

import java.util.concurrent.Executors

import akka.actor.Scheduler
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import tmt.sequencer.dsl.Script
import tmt.sequencer.models.EngineMsg.{ControlCommand, SequencerEvent}
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, Future}
import async.Async._

class Engine(script: Script, sequencerRef: ActorRef[SequencerMsg], system: ActorSystem[_]) {
  private implicit val ec: ExecutionContext = ExecutionContext.fromExecutorService(Executors.newSingleThreadExecutor())
  private implicit val timeout: Timeout     = Timeout(5.days)
  private implicit val scheduler: Scheduler = system.scheduler

  loop(nextStep())

  private def loop(stepF: Future[Step]): Unit = async {
    val step = await(stepF)
    step.command.name match {
      case x if x.startsWith("setup-") =>
        val commandResult = await(script.onSetup(step.command))
        sequencerRef ! UpdateStatus(step.id, StepStatus.Finished(commandResult))
        loop(nextStep())
      case x =>
    }
  }

  private def nextStep(): Future[Step] = (sequencerRef ? GetNext).map(_.step)

  def control(controlCommand: ControlCommand): Future[Unit] = controlCommand.name match {
    case "shutdown" => script.onShutdown()
    case _          => Future.unit
  }

  def event(sequencerEvent: SequencerEvent): Future[Unit] = script.onEvent(sequencerEvent)
}
