package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.EngineBehaviour._
import tmt.services.Command

import scala.collection.immutable.Queue

class EngineBehaviour(ctx: ActorContext[EngineAction]) extends MutableBehavior[EngineAction] {

  var queue: Queue[Command]          = Queue.empty
  var ref: Option[ActorRef[Command]] = None
  var paused: Boolean                = false

  override def onMessage(msg: EngineAction): Behavior[EngineAction] = {
    msg match {
      case Push(x) if ref.isEmpty || paused =>
        queue = queue.enqueue(x)
      case Push(x) =>
        ref.foreach(_ ! x)
        ref = None
      case Pull(replyTo) if hasNext =>
        val (elm, q) = queue.dequeue
        replyTo ! elm
        queue = q
      case Pull(replyTo)    => ref = Some(replyTo)
      case HasNext(replyTo) => replyTo ! hasNext
      case Pause            => paused = true
      case Resume =>
        paused = false
        ref.foreach(x => ctx.self ! Pull(x))
        ref = None
      case Reset =>
        queue = Queue.empty
    }
    this
  }

  def hasNext: Boolean = queue.nonEmpty && !paused
}

object EngineBehaviour {
  sealed trait EngineAction
  case class Push(command: Command)              extends EngineAction
  case class Pull(replyTo: ActorRef[Command])    extends EngineAction
  case class HasNext(replyTo: ActorRef[Boolean]) extends EngineAction
  case object Pause                              extends EngineAction
  case object Resume                             extends EngineAction
  case object Reset                              extends EngineAction

  def behavior: Behavior[EngineAction] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
