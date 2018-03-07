package tmt.sequencer.models

case class Step(command: Command, status: StepStatus, hasBreakpoint: Boolean) {
  def id: Id                                  = command.id
  def isPending: Boolean                      = status == StepStatus.Pending
  def addBreakpoint(): Step                   = copy(hasBreakpoint = true)
  def removeBreakpoint(): Step                = copy(hasBreakpoint = false)
  def withStatus(newStatus: StepStatus): Step = copy(status = newStatus)
}

object Step {
  def from(command: Command) = Step(command, StepStatus.Pending, hasBreakpoint = false)
}

sealed trait StepStatus

object StepStatus {
  case object Pending                        extends StepStatus
  case object InFlight                       extends StepStatus
  case class Finished(result: CommandResult) extends StepStatus
}

case class Id(value: String)
case class Command(id: Id, params: List[Int])
case class CommandResult(value: String)
