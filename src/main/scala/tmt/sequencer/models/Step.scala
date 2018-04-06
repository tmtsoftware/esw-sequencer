package tmt.sequencer.models

import tmt.sequencer.models.StepStatus.{Finished, InFlight, Pending}

case class Step(command: Command, status: StepStatus, hasBreakpoint: Boolean) {
  def id: Id              = command.id
  def isPending: Boolean  = status == StepStatus.Pending
  def isFinished: Boolean = status == StepStatus.Finished

  def addBreakpoint(): Step    = if (isPending) copy(hasBreakpoint = true) else this
  def removeBreakpoint(): Step = copy(hasBreakpoint = false)

  def withStatus(newStatus: StepStatus): Step = {
    (status, newStatus) match {
      case (Pending, InFlight)  => copy(status = newStatus)
      case (InFlight, Finished) => copy(status = newStatus)
      case _                    => this
    }
  }
}

object Step {
  def from(command: Command)                    = Step(command, StepStatus.Pending, hasBreakpoint = false)
  def from(commands: List[Command]): List[Step] = commands.map(command => from(command))
}

sealed trait StepStatus

object StepStatus {
  case object Pending  extends StepStatus
  case object InFlight extends StepStatus
  case object Finished extends StepStatus
}

case class Id(value: String)
case class Command(id: Id, name: String, params: List[Int], parentId: Id)

sealed trait CommandResponse {
  def id: Id
  def parentId: Id
}

object CommandResponse {
  case class Success(id: Id, parentId: Id, value: String)                    extends CommandResponse
  case class Failed(id: Id, parentId: Id, value: String)                     extends CommandResponse
  case class Composite(id: Id, parentId: Id, response: Set[CommandResponse]) extends CommandResponse
}

case class AggregateResponse(childResponses: Set[CommandResponse.Composite]) {
  def add(commandResponses: CommandResponse.Composite*): AggregateResponse  = copy(childResponses ++ commandResponses.toSet)
  def add(maybeResponse: Set[CommandResponse.Composite]): AggregateResponse = copy(childResponses ++ maybeResponse)
  def add(aggregateResponse: AggregateResponse)                             = AggregateResponse(childResponses ++ aggregateResponse.childResponses)
  def responses: Set[CommandResponse]                                       = childResponses.toSet[CommandResponse]

  def groupBy(parentId: Id): Set[CommandResponse.Composite] =
    childResponses
      .groupBy(_.parentId)
      .map {
        case (id, rs) => CommandResponse.Composite(id, parentId, rs.toSet[CommandResponse])
      }
      .toSet

}

object AggregateResponse {
  def empty                                           = AggregateResponse(Set.empty)
  def single(response: CommandResponse.Composite)     = AggregateResponse(Set(response))
  def multiple(responses: CommandResponse.Composite*) = AggregateResponse(responses.toSet)
}
