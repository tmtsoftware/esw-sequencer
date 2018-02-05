package tmt.sequencer

import akka.typed.scaladsl.Actor
import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor.MutableBehavior
import tmt.sequencer.EngineBehaviour.{EngineAction, Pull, Push}
import tmt.services.Command

import scala.collection.immutable.Queue

class EngineBehaviour extends MutableBehavior[EngineAction] {

  var queue: Queue[Command] = Queue.empty
  var ref: Option[ActorRef[Command]] = None

  override def onMessage(msg: EngineAction): Behavior[EngineAction] = {
    msg match {
      case Push(x) if ref.isEmpty      =>
        queue = queue.enqueue(x)
      case Push(x) =>
        ref.foreach(_ ! x)
        ref = None
      case Pull(replyTo) if queue.nonEmpty =>
        val (elm, q) = queue.dequeue
        replyTo ! elm
        queue = q
      case Pull(replyTo) =>
        ref = Some(replyTo)
    }
    this
  }
}

object EngineBehaviour {
  sealed trait EngineAction
  case class Push(command: Command) extends EngineAction
  case class Pull(replyTo: ActorRef[Command]) extends EngineAction

  def behaviour: Behavior[EngineAction] = Actor.mutable(ctx => new EngineBehaviour)
}
