package tmt.sequencer

import akka.actor.typed.ActorRef
import tmt.sequencer.models.EngineMsg.{CommandCompletion, StepCompletion}
import tmt.sequencer.models.{Command, CommandResult, EngineMsg}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CswServices(locationService: LocationService, _engineRef: => ActorRef[EngineMsg])(implicit ec: ExecutionContext) {
  private lazy val engineRef = _engineRef

  def setup(componentName: String, command: Command): Unit = {
    val assembly = locationService.resolve(componentName)
    commandCompletion(assembly.submit(command))
  }

  def stepComplete(commandResult: CommandResult): Unit = {
    engineRef ! StepCompletion(commandResult)
  }

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

  private def commandCompletion(commandResultF: Future[CommandResult]): Unit = commandResultF.onComplete {
    case Success(commandResult) => engineRef ! CommandCompletion(commandResult)
    case Failure(ex)            => engineRef ! CommandCompletion(CommandResult.Failed(ex.getMessage))
  }
}
