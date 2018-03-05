package tmt.sequencer.engine

import tmt.sequencer.Command

case class EngineCommand(command: Command, status: CommandStatus, position: Position)

object EngineCommand {
  def from(commands: List[Command]): List[EngineCommand] = commands.zipWithIndex.map {
    case (c, i) =>
      EngineCommand(c, CommandStatus.Remaining, Position(i + 1))
  }
}
