package tmt.sequencer

import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.{ExecutionContext, Future}

class CswServices(locationService: LocationService)(implicit ec: ExecutionContext) {
  def setup(componentName: String, command: Command): Future[CommandResult] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command)
  }
}
