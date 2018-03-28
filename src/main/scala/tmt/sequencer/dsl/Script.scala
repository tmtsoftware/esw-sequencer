package tmt.sequencer.dsl

import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.EngineMsg.SequencerEvent
import tmt.sequencer.models.{Command, CommandResults}

import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ControlDsl {
  def execute(x: Command): Future[CommandResults]
  def onEvent(event: SequencerEvent): Future[Unit]

  def shutdown(): Future[Unit] = onShutdown().map(_ => shutdownEc())

  protected def onShutdown(): Future[Unit]
}