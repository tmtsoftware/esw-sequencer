package tmt.sequencer.models

import akka.actor.typed.ActorRef
import tmt.sequencer.models.ScriptRunnerMsg.SequencerCommand

sealed trait SupervisorMsg

sealed trait ScriptRunnerMsg

object ScriptRunnerMsg {
  case class SequencerCommand(command: Command) extends ScriptRunnerMsg
  case class ControlCommand(name: String)       extends ScriptRunnerMsg with SupervisorMsg
  case class SequencerEvent(value: String)      extends ScriptRunnerMsg
}

sealed trait EngineMsg

object EngineMsg {
  case class Pull(replyTo: ActorRef[SequencerCommand]) extends EngineMsg
  case class HasNext(replyTo: ActorRef[Boolean])       extends EngineMsg

  sealed trait ExternalEngineMsg extends EngineMsg with SupervisorMsg

  case class Push(commands: List[Command]) extends ExternalEngineMsg
  case object Pause                        extends ExternalEngineMsg
  case object Resume                       extends ExternalEngineMsg
  case object Reset                        extends ExternalEngineMsg
}
