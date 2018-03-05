package tmt.sequencer.engine

import tmt.sequencer.Command

case class EngineState(data: Map[Position, EngineCommand], breakPoints: Set[Position], isPaused: Boolean) {
  //query
  def commands: List[EngineCommand] = {
    data.toList
      .sortBy { case (position, _) => position.value }
      .map { case (_, engineCommand) => engineCommand }
  }

  def processed: List[EngineCommand]  = commands.filter(_.status == CommandStatus.Processed)
  def inProgress: List[EngineCommand] = commands.filter(_.status == CommandStatus.InProgress)
  def remaining: List[EngineCommand]  = commands.filter(_.status == CommandStatus.Remaining)

  def hasNext: Boolean = remaining.nonEmpty && !isPaused

  def currentPosition: Option[Position] = {
    inProgress.lastOption.orElse(processed.lastOption).orElse(remaining.headOption).map(_.position)
  }

  //update
  private def update(command: EngineCommand): EngineState = copy(data + (command.position -> command))

  private def isValid(position: Position) = currentPosition match {
    case Some(minValidPos) if position.value > minValidPos.value =>
      true
    case _ => false //No-Op TODO could be exception
  }

  def upsert(command: EngineCommand): EngineState = {
    if (isValid(command.position)) {
      update(command)
    } else this
  }

  def deleteAt(position: Position): EngineState = {
    if (isValid(position)) {
      copy(data - position).removeBreakpoint(position)
    } else this
  }

  def addBreakpoint(position: Position): EngineState = {
    if (isValid(position)) {
      copy(breakPoints = breakPoints + position)
    } else this
  }

  def removeBreakpoint(position: Position): EngineState =
    copy(breakPoints = breakPoints - position)

  def pause(): EngineState  = copy(isPaused = true)
  def resume(): EngineState = copy(isPaused = false)
}

object EngineState {
  def from(commands: List[Command]): EngineState = {
    EngineState(EngineCommand.from(commands).map(x => x.position -> x).toMap, Set.empty, isPaused = false)
  }
}

case class Position(value: Int)
