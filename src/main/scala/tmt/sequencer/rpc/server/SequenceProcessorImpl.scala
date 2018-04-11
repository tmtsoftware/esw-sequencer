package tmt.sequencer.rpc.server

import akka.actor.{ActorSystem, Scheduler}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.util.Timeout
import tmt.sequencer.models.SequencerMsg.ProcessSequence
import tmt.sequencer.models.{AggregateResponse, Command, SequencerMsg}
import tmt.sequencer.rpc.api.SequenceProcessor

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try

class SequenceProcessorImpl(sequencer: ActorRef[SequencerMsg])(implicit system: ActorSystem) extends SequenceProcessor {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  override def submitSequence(commands: List[Command]): Future[AggregateResponse] = {
    val future: Future[Try[AggregateResponse]] = sequencer ? (x => ProcessSequence(commands, x))
    future.map(_.get)
  }
}
