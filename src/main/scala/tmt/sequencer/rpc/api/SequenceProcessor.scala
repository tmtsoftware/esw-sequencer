package tmt.sequencer.rpc.api

import tmt.sequencer.models.{AggregateResponse, Command}

import scala.concurrent.Future

trait SequenceProcessor {
  def submit(commands: List[Command]): Future[AggregateResponse]
}
