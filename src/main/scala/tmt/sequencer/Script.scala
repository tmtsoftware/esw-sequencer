package tmt.sequencer

import tmt.sequencer.models.{Command, CommandResult}
import tmt.sequencer.models.EngineMsg.SequencerEvent

abstract class Script(cs: CswServices) extends ControlDsl {
  def onSetup(x: Command): CommandResult
  def onObserve(x: Command): CommandResult
  def onShutdown(): Unit
  def onEvent(event: SequencerEvent): Unit
}
