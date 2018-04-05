package tmt.sequencer.models

import tmt.sequencer.models.StepStatus.{Finished, InFlight, Pending}

case class Step(command: Command, status: StepStatus, hasBreakpoint: Boolean, commandResult: CommandResult) {
  def id: Id             = command.id
  def isPending: Boolean = status == StepStatus.Pending

  def addBreakpoint(): Step    = if (isPending) copy(hasBreakpoint = true) else this
  def removeBreakpoint(): Step = copy(hasBreakpoint = false)

  def withStatus(newStatus: StepStatus): Step = {
    (status, newStatus) match {
      case (Pending, InFlight)  => copy(status = newStatus)
      case (InFlight, Finished) => copy(status = newStatus)
      case _                    => this
    }
  }

  def withResults(commandResult: CommandResult): Step = copy(commandResult = commandResult)
}

object Step {
  def from(command: Command)                    = Step(command, StepStatus.Pending, hasBreakpoint = false, CommandResult.Empty(command.id))
  def from(commands: List[Command]): List[Step] = commands.map(from)
}

sealed trait StepStatus

object StepStatus {
  case object Pending  extends StepStatus
  case object InFlight extends StepStatus
  case object Finished extends StepStatus
}

case class Id(value: String)
case class Command(id: Id, name: String, params: List[Int])

sealed trait CommandResult {
  def id: Id
}

object CommandResult {
  case class Success(id: Id, value: String) extends CommandResult
  case class Failed(id: Id, value: String)  extends CommandResult

  case class Composite(id: Id, results: List[CommandResult]) extends CommandResult {
    def prepend(commandResult: CommandResult): CommandResult = copy(results = commandResult :: results)
    def append(commandResult: CommandResult): CommandResult  = copy(results = results :+ commandResult)
  }

  case class Empty(id: Id) extends CommandResult
}
