package tmt.sequencer

import akka.actor.typed.ActorRef
import reactify.Channel
import tmt.sequencer.FutureExt.CommandResultFuture
import tmt.sequencer.models.CommandResult.{Empty, Multiple}
import tmt.sequencer.models.EngineMsg.{CommandCompletion, StepCompletion}
import tmt.sequencer.models.{Command, CommandResult, EngineMsg}
import tmt.sequencer.reactive.Engine

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal

class CswServices(locationService: LocationService, _engineRef: => ActorRef[EngineMsg], engine: => Engine)(
    implicit ec: ExecutionContext
) {
  private lazy val engineRef = _engineRef

  def setup(componentName: String, command: Command): Unit = {
    val assembly = locationService.resolve(componentName)
    trackCompletion(command, assembly.submit(command))
  }

  def setup2(componentName: String, command: Command): Channel[CommandResult] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command).resultChannel
  }

  def complete(commandResult: CommandResult): Unit = engine.resultCh := commandResult

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

  private def trackCompletion(command: Command, commandResultF: Future[CommandResult]): Unit =
    commandResultF.recover {
      case NonFatal(ex) => CommandResult.Failed(ex.getMessage)
    } map { commandResult =>
      engineRef ! CommandCompletion(command, commandResult)
    }

  def stepComplete(stepResults: List[CommandResult]): Unit = {
    val aggregatedResult = aggResult(stepResults)
    engineRef ! StepCompletion(aggregatedResult)
  }

  private def aggResult(stepResults: List[CommandResult]) = stepResults match {
    case Nil      => Empty
    case x :: Nil => x
    case xs       => Multiple(xs)
  }
}
