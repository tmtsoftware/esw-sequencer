package tmt.sequencer.dsl

import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{Command, CommandResults}

import scala.concurrent.Future

abstract class Script(cs: CswServices, observationMode: String) extends Strand {
  def execute(x: Command): Future[CommandResults]
  def shutdown(): Future[Unit] = onShutdown().map(_ => shutdownEc())

  protected def onShutdown(): Future[Unit]
}
