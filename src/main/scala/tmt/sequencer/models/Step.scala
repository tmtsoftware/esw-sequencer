package tmt.sequencer.models

import tmt.sequencer.models.StepStatus.{Finished, InFlight, Pending}

case class Step(command: Command, status: StepStatus, hasBreakpoint: Boolean) {
  def id: Id             = command.id
  def isPending: Boolean = status == StepStatus.Pending

  def addBreakpoint(): Step    = if (isPending) copy(hasBreakpoint = true) else this
  def removeBreakpoint(): Step = copy(hasBreakpoint = false)

  def withStatus(newStatus: StepStatus): Step = {
    (status, newStatus) match {
      case (Pending, InFlight)     => copy(status = newStatus)
      case (InFlight, Finished(x)) => copy(status = newStatus)
      case _                       => this
    }
  }
}

object Step {
  def from(command: Command)                    = Step(command, StepStatus.Pending, hasBreakpoint = false)
  def from(commands: List[Command]): List[Step] = commands.map(from)
}

sealed trait StepStatus

object StepStatus {
  case object Pending                        extends StepStatus
  case object InFlight                       extends StepStatus
  case class Finished(result: CommandResults) extends StepStatus
}

case class Id(value: String)
case class Command(id: Id, name: String, params: List[Int])

sealed trait CommandResult
object CommandResult {
  case class Success(value: String) extends CommandResult
  case class Failed(value: String)  extends CommandResult
}

case class CommandResults(values: List[CommandResult])
object CommandResults {
  def empty = CommandResults(Nil)
}
