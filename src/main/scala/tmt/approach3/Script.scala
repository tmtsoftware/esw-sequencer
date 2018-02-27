package tmt.approach3

import tmt.sequencer.CommandService
import tmt.services.Command

abstract class Script(cs: CommandService) extends Dsl {
  def onCommand(x: Command): Unit
  def onShutdown(): Unit
}
