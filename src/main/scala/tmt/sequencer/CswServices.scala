package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.sequencer.models.{Command, CommandResult, Step}

import scala.concurrent.ExecutionContext

class CswServices(locationService: LocationService, sequencer: Sequencer)(implicit ec: ExecutionContext) {
  def setup(componentName: String, command: Command): CommandResult = {
    locationService.resolve(componentName).setup(command)
  }.await

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)

  def hasNext: Boolean = sequencer.hasNext
  def next: Step       = sequencer.next
}
