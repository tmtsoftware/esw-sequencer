package tmt.sequencer

import tmt.sequencer.models.{Command, CommandResult}
import tmt.sequencer.models.EngineMsg.SequencerEvent

abstract class Script(cs: CswServices) extends ControlDsl {
  def onSetup(x: Command): Unit
  def onCommandCompletion(commandResult: CommandResult): Unit
  def onShutdown(): Unit
  def onEvent(event: SequencerEvent): Unit
}
