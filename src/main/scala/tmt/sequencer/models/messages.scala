package tmt.sequencer.models

import akka.actor.typed.ActorRef
import tmt.sequencer.models.ScriptRunnerMsg.SequencerCommand

sealed trait SupervisorMsg

sealed trait ScriptRunnerMsg

object ScriptRunnerMsg {
  case class SequencerCommand(step: Step)  extends ScriptRunnerMsg
  case class ControlCommand(name: String)  extends ScriptRunnerMsg with SupervisorMsg
  case class SequencerEvent(value: String) extends ScriptRunnerMsg
}

sealed trait EngineMsg

object EngineMsg {
  case class Pull(replyTo: ActorRef[SequencerCommand])        extends EngineMsg
  case class UpdateStatus(stepId: Id, stepStatus: StepStatus) extends EngineMsg
  case class HasNext(replyTo: ActorRef[Boolean])              extends EngineMsg

  sealed trait ExternalEngineMsg extends EngineMsg with SupervisorMsg

  case class Push(commands: List[Command])                extends ExternalEngineMsg
  case object Pause                                       extends ExternalEngineMsg
  case object Resume                                      extends ExternalEngineMsg
  case object Reset                                       extends ExternalEngineMsg
  case class Replace(id: Id, commands: List[Command])     extends ExternalEngineMsg
  case class Prepend(commands: List[Command])             extends ExternalEngineMsg
  case class Delete(ids: List[Id])                        extends ExternalEngineMsg
  case class InsertAfter(id: Id, commands: List[Command]) extends ExternalEngineMsg
  case class AddBreakpoints(ids: List[Id])                extends ExternalEngineMsg
  case class RemoveBreakpoints(ids: List[Id])             extends ExternalEngineMsg
}
