package tmt.sequencer

import akka.actor.typed.ActorRef
import tmt.sequencer.ScriptRunnerMsg.SequencerCommand

sealed trait SupervisorMsg

sealed trait ScriptRunnerMsg

object ScriptRunnerMsg {
  case class SequencerCommand(command: Command) extends ScriptRunnerMsg
  case class ControlCommand(name: String)       extends ScriptRunnerMsg with SupervisorMsg
  case class SequencerEvent(value: String)      extends ScriptRunnerMsg
}

sealed trait EngineMsg
object EngineMsg {
  case class Push(commands: List[Command])             extends EngineMsg with SupervisorMsg
  case class Pull(replyTo: ActorRef[SequencerCommand]) extends EngineMsg
  case class HasNext(replyTo: ActorRef[Boolean])       extends EngineMsg
  case object Pause                                    extends EngineMsg
  case object Resume                                   extends EngineMsg
  case object Reset                                    extends EngineMsg
}
