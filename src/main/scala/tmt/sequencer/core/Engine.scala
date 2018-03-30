package tmt.sequencer.core

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern.Askable
import akka.actor.{ActorSystem, Scheduler}
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import akka.util.Timeout
import tmt.sequencer.ScriptImports.CswServices
import tmt.sequencer.dsl.{Script, ScriptFactory}
import tmt.sequencer.models.EngineMsg.ControlCommand
import tmt.sequencer.models.SequencerMsg.{GetNext, UpdateStatus}
import tmt.sequencer.models._

import scala.async.Async._
import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble

class Engine(scriptFactory: ScriptFactory, cswServices: CswServices, sequencerRef: ActorRef[SequencerMsg])(
    implicit system: ActorSystem,
    mat: Materializer
) {
  private implicit val timeout: Timeout     = Timeout(5.days)
  private implicit val scheduler: Scheduler = system.scheduler

  import mat.executionContext

  private val script: Script = scriptFactory.get(cswServices)

  Source.repeat(()).mapAsync(1)(_ => execute()).runForeach(_ => ())

  private def execute(): Future[Unit] = async {
    val step = await((sequencerRef ? GetNext).map(_.step))
    step.command.name match {
      case x if x.startsWith("setup-") =>
        val commandResult = await(script.execute(step.command))
        sequencerRef ! UpdateStatus(step.id, StepStatus.Finished(commandResult))
      case x =>
    }
  }

  def control(controlCommand: ControlCommand): Future[Unit] = controlCommand.name match {
    case "shutdown" => script.shutdown()
    case _          => Future.unit
  }
}
