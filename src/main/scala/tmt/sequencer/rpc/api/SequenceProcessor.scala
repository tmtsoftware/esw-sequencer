package tmt.sequencer.rpc.api

import tmt.sequencer.models.{AggregateResponse, Command}

import scala.concurrent.Future

trait SequenceProcessor {
  def submitSequence(commands: List[Command]): Future[AggregateResponse]
}
