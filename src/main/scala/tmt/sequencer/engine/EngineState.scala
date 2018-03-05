package tmt.sequencer.engine

import tmt.sequencer.Command

import scala.collection.immutable
import scala.collection.immutable.Queue

case class EngineState(queue: Queue[Command]) {
  def statusQuery(): StatusResponse = {
    val processed: immutable.Seq[Command]  = queue.filter(command => command.status == CommandStatus.Processed)
    val inProgress: immutable.Seq[Command] = queue.filter(command => command.status == CommandStatus.InProgress)
    val remaining: immutable.Seq[Command]  = queue.filter(command => command.status == CommandStatus.Remaining)

    StatusResponse(processed.toList, inProgress.toList, remaining.toList)
  }
}

case class StatusResponse(processed: immutable.List[Command],
                          inProgress: immutable.List[Command],
                          remaining: immutable.List[Command]) {
  override def toString: String = {
    "Processed commands - " + processed + "\n" +
    "In-progress commands - " + inProgress + "\n" +
    "Remaining commands - " + remaining + "\n"
  }
}
