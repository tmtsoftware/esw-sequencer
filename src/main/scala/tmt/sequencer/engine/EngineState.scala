package tmt.sequencer.engine

import tmt.sequencer.{Command, Id}

case class EngineState(commands: List[EngineCommand]) {

  //query

  def hasNext: Boolean                   = commands.exists(_.isPending) && !isPaused
  def isPaused: Boolean                  = nextCommand.exists(_.hasBreakpoint)
  def nextCommand: Option[EngineCommand] = commands.find(_.isPending)

  //update

  def replace(id: Id, command: Command): EngineState               = replace(id, EngineCommand.from(command))
  private def replace(id: Id, command: EngineCommand): EngineState = insertAfter(id, command).delete(id)

  def prepend(command: Command): EngineState = insert(!_.isPending, EngineCommand.from(command))
  def append(command: Command): EngineState  = copy(commands = commands :+ EngineCommand.from(command))

  def delete(id: Id): EngineState = {
    val (pre, post) = commands.span(_.command.id != id)
    copy(commands = pre ::: post.tail).removeBreakpoint(id)
  }

  def insertAfter(id: Id, command: Command): EngineState               = insertAfter(id, EngineCommand.from(command))
  private def insertAfter(id: Id, command: EngineCommand): EngineState = insert(!_.contains(id), command)

  private def insert(predicate: EngineCommand => Boolean, engineCommand: EngineCommand): EngineState = {
    val (pre, post) = commands.span(predicate)
    val newCommands = pre ::: post.headOption.toList ::: (engineCommand :: post.tail)
    copy(commands = newCommands)
  }

  def addBreakpoint(id: Id): EngineState = transform(id, _.addBreakpoint())

  def removeBreakpoint(id: Id): EngineState = transform(id, _.removeBreakpoint())

  private def transform(id: Id, f: EngineCommand => EngineCommand): EngineState = {
    commands.find(x => x.contains(id) && x.isPending) match {
      case Some(engineCommand) => replace(id, f(engineCommand))
      case None                => this
    }
  }

  def pause(): EngineState = updateWith(addBreakpoint)

  def resume(): EngineState = updateWith(removeBreakpoint)

  private def updateWith(f: Id => EngineState): EngineState = nextCommand match {
    case Some(engineCommand) => f(engineCommand.command.id)
    case None                => this
  }
}
