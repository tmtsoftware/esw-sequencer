package tmt.sequencer.engine

sealed trait CommandStatus

object CommandStatus {

  case object Processed extends CommandStatus

  case object InProgress extends CommandStatus

  case object Remaining extends CommandStatus

}
