package tmt.approach3

import tmt.approach3.ScriptRunnerBehavior.SequencerEvent
import tmt.sequencer.CommandService
import tmt.services.Command

abstract class Script(cs: CommandService) extends Dsl {
  def onSetup(x: Command): Unit
  def onObserve(x: Command): Unit
  def onShutdown(): Unit
  def onEvent(event: SequencerEvent): Unit
}
