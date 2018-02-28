package tmt.sequencer

import tmt.sequencer.ScriptRunnerMsg.SequencerEvent

abstract class Script(cs: CommandService) extends Dsl {
  def onSetup(x: Command): Unit
  def onObserve(x: Command): Unit
  def onShutdown(): Unit
  def onEvent(event: SequencerEvent): Unit
}
