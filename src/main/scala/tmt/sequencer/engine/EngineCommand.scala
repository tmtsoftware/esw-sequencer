package tmt.sequencer.engine

import tmt.sequencer.{Command, Id}

case class EngineCommand(command: Command, status: CommandStatus, hasBreakpoint: Boolean) {
  def isPending: Boolean                                  = status == CommandStatus.Pending
  def contains(id: Id): Boolean                           = command.id == id
  def addBreakpoint(): EngineCommand                      = copy(hasBreakpoint = true)
  def removeBreakpoint(): EngineCommand                   = copy(hasBreakpoint = false)
  def withStatus(newStatus: CommandStatus): EngineCommand = copy(status = newStatus)
}

object EngineCommand {
  def from(command: Command) = EngineCommand(command, CommandStatus.Pending, hasBreakpoint = false)
}
