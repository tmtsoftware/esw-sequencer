package tmt.sequencer.api

import tmt.sequencer.models.{AggregateResponse, Command}

import scala.concurrent.Future

trait SequenceFeeder {
  def feed(commands: List[Command]): Future[AggregateResponse]
}
