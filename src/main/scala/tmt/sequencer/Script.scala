package tmt.sequencer

import tmt.sequencer.models.EngineMsg.SequencerEvent
import tmt.sequencer.models.{Command, CommandResults}

import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ControlDsl {
  def onSetup(x: Command): Future[CommandResults]
  def onShutdown(): Future[Unit]
  def onEvent(event: SequencerEvent): Future[Unit]
}
