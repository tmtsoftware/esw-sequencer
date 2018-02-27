package tmt.approach3

import tmt.sequencer.CommandService
import tmt.services.Command

abstract class Script(cs: CommandService) extends Dsl {
  def onSetup(x: Command): Unit
  def onObserve(x: Command): Unit
  def onShutdown(): Unit
}
