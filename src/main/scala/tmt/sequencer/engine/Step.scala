package tmt.sequencer.engine

import tmt.sequencer.{Command, Id}

case class Step(command: Command, status: StepStatus, hasBreakpoint: Boolean) {
  def isPending: Boolean                      = status == StepStatus.Pending
  def hasId(id: Id): Boolean                  = command.id == id
  def addBreakpoint(): Step                   = copy(hasBreakpoint = true)
  def removeBreakpoint(): Step                = copy(hasBreakpoint = false)
  def withStatus(newStatus: StepStatus): Step = copy(status = newStatus)
}

object Step {
  def from(command: Command) = Step(command, StepStatus.Pending, hasBreakpoint = false)
}
