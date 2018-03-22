package tmt.sequencer

import akka.actor.typed.ActorRef
import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.{Command, CommandResult, EngineMsg, Step}

import scala.concurrent.ExecutionContext

class CswServices(locationService: LocationService, engineRef: => ActorRef[EngineMsg])(implicit ec: ExecutionContext) {
  def setup(componentName: String, command: Command): CommandResult = {
    val assembly = locationService.resolve(componentName)
    //convert command into Controlcommand Setup
    assembly.submit(command)
  }.await

  def observe(componentName: String, command: Command): CommandResult = {
    val assembly = locationService.resolve(componentName)
    //convert command into Controlcommand Observe
    assembly.submit(command)
  }.await

  def setupAndSubscribe(componentName: String, command: Command): CommandResult = {
    val assembly = locationService.resolve(componentName)
    //convert command into Controlcommand Observe
    assembly.submit(command)
  }.await

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

}
