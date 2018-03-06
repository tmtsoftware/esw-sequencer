package tmt.sequencer.engine

import tmt.sequencer.Command

case class EngineCommand(command: Command, status: CommandStatus)

object EngineCommand {
  def from(commands: List[Command]): List[EngineCommand] = commands.map(EngineCommand(_, CommandStatus.Remaining))

  def from(command: Command) = EngineCommand(command, CommandStatus.Remaining)
}
