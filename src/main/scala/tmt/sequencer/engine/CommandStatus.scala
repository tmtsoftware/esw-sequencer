package tmt.sequencer.engine

sealed trait CommandStatus

object CommandStatus {

  case object Finished extends CommandStatus

  case object InFlight extends CommandStatus

  case object Pending extends CommandStatus

}
