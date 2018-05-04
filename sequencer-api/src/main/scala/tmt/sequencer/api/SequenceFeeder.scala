package tmt.sequencer.api

import sequencer_protobuf.command.PbMyJson
import tmt.sequencer.models.{AggregateResponse, CommandList, Msg}

import scala.concurrent.Future

trait SequenceFeeder {
  def feed(commandList: CommandList): Future[AggregateResponse]
  def testJsonApi(msg: Msg): Future[Msg]
  def testPbWithJsonApi(myJson: PbMyJson): Future[PbMyJson]
}
