package tmt.sequencer.api

import sequencer_protobuf.command.PbMyJson
import tmt.sequencer.models.{AggregateResponse, CommandList, Msg}

import scala.concurrent.Future

trait SequenceFeeder {
  def feed(commandList: CommandList): Future[AggregateResponse]
  def sayHello(msg: Msg): Future[Msg]
  def sayHello2(myJson: PbMyJson): Future[PbMyJson]
}
