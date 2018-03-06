package tmt.sequencer.engine

import tmt.sequencer.Command

case class EngineState(data: Map[Position, EngineCommand], breakPoints: Set[Position], isPaused: Boolean) {
  require(breakPoints subsetOf data.keys.toSet, "breakpoints and data are out of sync")

  //query
  def commands: List[EngineCommand] = {
    data.toList
      .sortBy { case (position, _) => position.value }
      .map { case (_, engineCommand) => engineCommand }
  }

  def processed: List[EngineCommand]  = commands.filter(_.status == CommandStatus.Finished)
  def inProgress: List[EngineCommand] = commands.filter(_.status == CommandStatus.InFlight)
  def remaining: List[EngineCommand]  = commands.filter(_.status == CommandStatus.Remaining)

  def hasNext: Boolean = remaining.nonEmpty && !isPaused

  //update
  def upsert(command: EngineCommand): EngineState = {
    if (isValidToOperateOn(command.position)) copy(data + (command.position -> command)) else this
  }

  def deleteAt(position: Position): EngineState = {
    if (isValidToOperateOn(position)) copy(data - position).removeBreakpoint(position) else this
  }

  def addBreakpoint(position: Position): EngineState = {
    if (isValidToOperateOn(position)) copy(breakPoints = breakPoints + position) else this
  }

  def removeBreakpoint(position: Position): EngineState = copy(breakPoints = breakPoints - position)

  def pause(): EngineState = copy(isPaused = true)

  def resume(): EngineState = copy(isPaused = false)

  private def isValidToOperateOn(position: Position) = nextAvailablePosition match {
    case Some(minValidPos) if position.value >= minValidPos.value => true
    case _                                                        => false //No-Op TODO could be exception
  }

  private def nextAvailablePosition: Option[Position] = {
    remaining.headOption.map(_.position)
  }
}

object EngineState {
  def from(commands: List[Command]): EngineState = {
    EngineState(EngineCommand.from(commands).map(x => x.position -> x).toMap, Set.empty, isPaused = false)
  }
}

case class Position(value: Int)
