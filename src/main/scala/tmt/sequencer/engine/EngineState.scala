package tmt.sequencer.engine

import tmt.sequencer.{Command, Id}

case class EngineState(commands: List[EngineCommand], breakPoints: Set[Id], isPaused: Boolean) {
  require(breakPoints subsetOf commands.map(_.command.id).toSet, "breakpoints and data are out of sync")

  def hasNext: Boolean = commands.exists(_.status == CommandStatus.Remaining) && !isPaused

  //update
  def insertAfter(id: Id, command: Command): EngineState = insertAfter(_.command.id != id, command)
  def prepend(command: Command): EngineState             = insertAfter(_.status != CommandStatus.Remaining, command)
  def append(command: Command): EngineState              = copy(commands = commands :+ EngineCommand.from(command))
  def delete(id: Id): EngineState = {
    val (pre, post) = commands.span(_.command.id != id)
    copy(commands = pre ::: post.tail).removeBreakpoint(id)
  }
  def replace(id: Id, command: Command): EngineState = insertAfter(id, command).delete(id)

  private def insertAfter(predicate: EngineCommand => Boolean, command: Command): EngineState = {
    val (pre, post) = commands.span(predicate)
    val newCommands = pre ::: post.headOption.toList ::: (EngineCommand.from(command) :: post.tail)
    copy(commands = newCommands)
  }

  def addBreakpoint(id: Id): EngineState = {
    val validCommandToOperateOn =
      commands.exists(engineCommand => (engineCommand.command.id == id) && (engineCommand.status == CommandStatus.Remaining))
    if (validCommandToOperateOn) copy(breakPoints = breakPoints + id) else this
  }

  def removeBreakpoint(id: Id): EngineState = copy(breakPoints = breakPoints - id)

  def pause(): EngineState = copy(isPaused = true)

  def resume(): EngineState = copy(isPaused = false)
}
