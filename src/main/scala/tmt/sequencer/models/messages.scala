package tmt.sequencer.models

import akka.actor.typed.ActorRef
import tmt.sequencer.models.EngineMsg.SequencerCommand

sealed trait SupervisorMsg

sealed trait EngineMsg

object EngineMsg {
  case class SequencerCommand(step: Step)  extends EngineMsg
  case class ControlCommand(name: String)  extends EngineMsg with SupervisorMsg
  case class SequencerEvent(value: String) extends EngineMsg
}

sealed trait SequencerMsg

object SequencerMsg {
  case class GetNext(replyTo: ActorRef[SequencerCommand])     extends SequencerMsg
  case class UpdateStatus(stepId: Id, stepStatus: StepStatus) extends SequencerMsg
  case class HasNext(replyTo: ActorRef[Boolean])              extends SequencerMsg

  sealed trait ExternalSequencerMsg extends SequencerMsg with SupervisorMsg

  case class Add(commands: List[Command])                 extends ExternalSequencerMsg
  case object Pause                                       extends ExternalSequencerMsg
  case object Resume                                      extends ExternalSequencerMsg
  case object Reset                                       extends ExternalSequencerMsg
  case class Replace(id: Id, commands: List[Command])     extends ExternalSequencerMsg
  case class Prepend(commands: List[Command])             extends ExternalSequencerMsg
  case class Delete(ids: List[Id])                        extends ExternalSequencerMsg
  case class InsertAfter(id: Id, commands: List[Command]) extends ExternalSequencerMsg
  case class AddBreakpoints(ids: List[Id])                extends ExternalSequencerMsg
  case class RemoveBreakpoints(ids: List[Id])             extends ExternalSequencerMsg
  case class GetSequence(replyTo: ActorRef[Sequence])     extends ExternalSequencerMsg
}
