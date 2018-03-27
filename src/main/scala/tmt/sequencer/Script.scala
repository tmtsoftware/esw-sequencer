package tmt.sequencer

import tmt.sequencer.models.{Command, CommandResult}
import tmt.sequencer.models.EngineMsg.SequencerEvent

import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ControlDsl {
  def onSetup(x: Command): Future[CommandResult]
  def onCommandCompletion(command: Command, commandResult: CommandResult): Unit
  def onShutdown(): Future[Unit]
  def onEvent(event: SequencerEvent): Future[Unit]
}
