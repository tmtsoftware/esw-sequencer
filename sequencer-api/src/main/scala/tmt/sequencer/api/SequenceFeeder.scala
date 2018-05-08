package tmt.sequencer.api

import tmt.sequencer.models.{AggregateResponse, CommandList, Msg}

import scala.concurrent.Future

trait SequenceFeeder {
  def feed(commandList: CommandList): Future[AggregateResponse]
  def testJsonApi(msg: Msg): Future[Msg]
}
