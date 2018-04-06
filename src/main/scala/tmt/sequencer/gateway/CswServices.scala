package tmt.sequencer.gateway

import akka.Done
import akka.actor.Cancellable
import akka.stream.scaladsl.{Keep, Sink, Source}
import akka.stream.{KillSwitch, KillSwitches, Materializer, ThrottleMode}
import tmt.sequencer.ScriptImports.Script
import tmt.sequencer.core.{Engine, Sequencer}
import tmt.sequencer.models.{Command, CommandResponse}

import scala.async.Async.{async, await}
import scala.concurrent.duration.{DurationDouble, FiniteDuration}
import scala.concurrent.{ExecutionContext, Future}

class CswServices(sequencer: Sequencer,
                  engine: Engine,
                  locationService: LocationService,
                  val sequencerId: String,
                  val observingMode: String)(
    implicit mat: Materializer
) {

  def nextIf(f: Command => Boolean): Future[Option[Command]] =
    async {
      val hasNext = await(sequencer.maybeNext).map(_.command).exists(f)
      if (hasNext) Some(await(sequencer.next).command) else None
    }(mat.executionContext)

  def setup(componentName: String, command: Command): Future[CommandResponse] = {
    val assembly = locationService.resolve(componentName)
    assembly.submit(command)(mat.executionContext)
  }

  def subscribe(key: String)(callback: SequencerEvent => Done)(implicit strandEc: ExecutionContext): KillSwitch = {
    subscribeAsync(key)(e => Future(callback(e))(strandEc))
  }

  def publish(every: FiniteDuration)(eventGeneratorBlock: => SequencerEvent)(implicit strandEc: ExecutionContext): Cancellable = {
    publishAsync(every)(Future(eventGeneratorBlock)(strandEc))
  }

  private def subscribeAsync(key: String)(callback: SequencerEvent => Future[Done]): KillSwitch = {
    Source
      .fromIterator(() => Iterator.from(1))
      .map(x => SequencerEvent(key, x.toString))
      .throttle(1, 10.second, 1, ThrottleMode.shaping)
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
