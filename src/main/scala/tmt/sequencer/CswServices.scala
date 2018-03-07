package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.ExecutionContext

class CswServices(locationService: LocationService, engine: Engine)(implicit ec: ExecutionContext) {
  def setup(componentName: String, command: Command): CommandResult = {
    locationService.resolve(componentName).setup(command)
  }.await

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

  def hasNext: Boolean    = engine.hasNext
  def pullNext(): Command = engine.pullNext()
}
