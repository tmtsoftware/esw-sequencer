package tmt.sequencer.engine

sealed trait StepStatus

object StepStatus {
  case object Finished extends StepStatus
  case object InFlight extends StepStatus
  case object Pending  extends StepStatus
}
