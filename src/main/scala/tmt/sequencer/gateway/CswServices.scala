package tmt.sequencer.gateway

import akka.actor.Cancellable
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{KillSwitch, KillSwitches, Materializer, ThrottleMode}
import tmt.sequencer.models.EngineMsg.SequencerEvent
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.duration.{DurationDouble, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class CswServices(locationService: LocationService)(implicit mat: Materializer) {
  def setup(componentName: String, command: Command): Future[CommandResult] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command)(mat.executionContext)
  }

  def subscribe(key: String)(callback: SequencerEvent => Unit)(implicit strandEc: ExecutionContext): KillSwitch = {
    subscribeAsync(key)(e => Future(callback(e))(strandEc))
  }

  def publish(every: FiniteDuration)(eventGeneratorBlock: => SequencerEvent)(implicit strandEc: ExecutionContext): Cancellable = {
    publishAsync(every)(Future(eventGeneratorBlock)(strandEc))
  }

  private def subscribeAsync(key: String)(callback: SequencerEvent => Future[Unit]): KillSwitch = {
    Source
      .fromIterator(() => Iterator.from(1))
      .map(x => SequencerEvent(key, x.toString))
      .throttle(1, 3.second, 1, ThrottleMode.shaping)
      .mapAsync(1)(callback)
      .viaMat(KillSwitches.single)(Keep.right)
      .to(Sink.ignore)
      .run()
  }

  private def publishAsync(every: FiniteDuration)(eventGeneratorBlock: => Future[SequencerEvent]): Cancellable = {
    val source = Source.tick(0.millis, every, ()).mapAsync(1)(_ => eventGeneratorBlock)
    val sink = Sink.foreach[SequencerEvent] {
      case SequencerEvent(k, v) => println(s"published: event=$v on key=$k")
    }
    source.to(sink).run()
  }
}
