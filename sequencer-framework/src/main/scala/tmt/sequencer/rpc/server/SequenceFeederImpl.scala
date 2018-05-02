package tmt.sequencer.rpc.server

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.AskPattern._
import akka.actor.{ActorSystem, Scheduler}
import akka.util.Timeout
import tmt.sequencer.api.SequenceFeeder
import tmt.sequencer.messages.SequencerMsg
import tmt.sequencer.messages.SequencerMsg.ProcessSequence
import tmt.sequencer.models.{AggregateResponse, CommandList, Msg}

import scala.concurrent.Future
import scala.concurrent.duration.DurationLong
import scala.util.Try

class SequenceFeederImpl(sequencer: ActorRef[SequencerMsg])(implicit system: ActorSystem) extends SequenceFeeder {
  private implicit val timeout: Timeout     = Timeout(10.hour)
  private implicit val scheduler: Scheduler = system.scheduler
  import system.dispatcher

  override def feed(commandList: CommandList): Future[AggregateResponse] = {
    val future: Future[Try[AggregateResponse]] = sequencer ? (x => ProcessSequence(commandList.commands.toList, x))
    future.map(_.get)
  }

  override def sayHello(msg: Msg): Future[Msg] = {
    Future{
      msg
    }
  }
}
