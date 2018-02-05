package tmt.sequencer

import tmt.sequencer.FutureExt.RichFuture
import tmt.services.{Command, CommandResponse, LocationService}

import scala.concurrent.{ExecutionContext, Future}

class CommandService(locationService: LocationService)(implicit ec: ExecutionContext) {
  def setup(componentName: String, params: List[Int]): CommandResponse = {
    locationService.resolve(componentName).submit(Command("setup", params))
  }.await

  def observe(componentName: String, params: List[Int]): CommandResponse = {
    locationService.resolve(componentName).submit(Command("observe", params))
  }.await

  def split(params: List[Int]): (List[Int], List[Int]) = params.partition(_ % 2 != 0)
}
