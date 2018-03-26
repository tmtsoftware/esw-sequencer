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

  @volatile
  private var _stepStracker: StepTracker = StepTracker(Nil, 0)

  def stepTracker: StepTracker = _stepStracker

  def setup(componentName: String, command: Command): Unit = {
    val assembly = locationService.resolve(componentName)
    _stepStracker = _stepStracker.sent()
    trackCompletion(assembly.submit(command))
  }

  def setup2(componentName: String, command: Command): Channel[CommandResult] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command).resultChannel
  }

  def complete(commandResult: CommandResult): Unit = engine.resultCh := commandResult

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

  private def trackCompletion(commandResultF: Future[CommandResult]): Unit =
    commandResultF.recover {
      case NonFatal(ex) => CommandResult.Failed(ex.getMessage)
    } map { commandResult =>
      engineRef ! CommandCompletion(commandResult)
      _stepStracker = _stepStracker.received(commandResult)
      if (_stepStracker.isFinished) {
        engineRef ! StepCompletion(_stepStracker.aggResult)
      }
    }
}

case class StepTracker(results: List[CommandResult], commandCount: Int) {
  def sent(): StepTracker                                 = copy(commandCount = commandCount + 1)
  def received(commandResult: CommandResult): StepTracker = copy(results :+ commandResult, commandCount - 1)
  def isFinished: Boolean                                 = commandCount == 0 && results.nonEmpty

  def aggResult: CommandResult = results match {
    case Nil      => Empty
    case x :: Nil => x
    case xs       => Multiple(xs)
  }
}
