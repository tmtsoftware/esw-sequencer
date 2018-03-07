package tmt.sequencer

import tmt.sequencer.models.Command
import tmt.sequencer.models.ScriptRunnerMsg.SequencerEvent

abstract class Script(cs: CswServices) extends ControlDsl {
  def onSetup(x: Command): Unit
  def onObserve(x: Command): Unit
  def onShutdown(): Unit
  def onEvent(event: SequencerEvent): Unit
}
