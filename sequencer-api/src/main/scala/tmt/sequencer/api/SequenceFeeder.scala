package tmt.sequencer.api

import tmt.sequencer.models.{AggregateResponse, CommandList}

import scala.concurrent.Future

trait SequenceFeeder {
  def feed(commandList: CommandList): Future[AggregateResponse]
}

object SequenceFeeder {
  val ApiName = "feeder"
  val Feed    = "feed"
}
