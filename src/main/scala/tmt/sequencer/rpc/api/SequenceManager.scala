package tmt.sequencer.rpc.api

import tmt.sequencer.models._

import scala.concurrent.Future

trait SequenceManager {
  def addAll(commands: List[Command]): Future[Unit]
  def pause(): Future[Unit]
  def resume(): Future[Unit]
  def reset(): Future[Unit]
  def sequence: Future[Sequence]
  def delete(ids: List[Id]): Future[Unit]
  def addBreakpoints(ids: List[Id]): Future[Unit]
  def removeBreakpoints(ids: List[Id]): Future[Unit]
  def insertAfter(id: Id, commands: List[Command]): Future[Unit]
  def prepend(commands: List[Command]): Future[Unit]
  def replace(id: Id, commands: List[Command]): Future[Unit]
  def shutdown(): Future[Unit]
}
