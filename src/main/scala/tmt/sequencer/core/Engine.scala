package tmt.sequencer.core

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import tmt.sequencer.dsl.Script
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._

import scala.async.Async._
import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class Engine(script: Script, sequencerRef: ActorRef[SequencerMsg], system: ActorSystem) {
  private implicit val timeout: Timeout     = Timeout(5.days)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  loop(nextStep())

  private def loop(stepF: Future[Step]): Unit = async {
    val step = await(stepF)
    step.command.name match {
      case x if x.startsWith("setup-") =>
        val commandResult = await(script.execute(step.command))
        sequencerRef ! UpdateStatus(step.id, StepStatus.Finished(commandResult))
        loop(nextStep())
      case x =>
    }
  }

  private def nextStep(): Future[Step] = (sequencerRef ? GetNext).map(_.step)

  def control(controlCommand: ControlCommand): Future[Unit] = controlCommand.name match {
    case "shutdown" => script.shutdown()
    case _          => Future.unit
  }
}
