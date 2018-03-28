package tmt.sequencer.gateway

import akka.Done
import akka.stream.{Materializer, ThrottleMode}
import akka.stream.scaladsl.Source
import tmt.sequencer.models.EngineMsg.SequencerEvent
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.duration.DurationDouble
import scala.concurrent.{ExecutionContext, Future}

class CswServices(locationService: LocationService)(implicit mat: Materializer) {
  def setup(componentName: String, command: Command): Future[CommandResult] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command)(mat.executionContext)
  }

  def subscribe(key: String)(callback: SequencerEvent => Unit)(implicit strandEc: ExecutionContext): Future[Done] = {
    subscribeAsync(key)(e => Future(callback(e))(strandEc))
  }

  def subscribeAsync(key: String)(callback: SequencerEvent => Future[Unit]): Future[Done] = {
    Source
      .fromIterator(() => Iterator.from(1))
      .map(x => SequencerEvent(x.toString))
      .throttle(1, 100.milli, 1, ThrottleMode.shaping)
      .mapAsync(1)(callback)
      .runForeach(_ => ())
  }
}
