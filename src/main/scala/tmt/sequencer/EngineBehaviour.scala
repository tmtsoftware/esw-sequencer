package tmt.shared.engine

import akka.typed.scaladsl.Actor.MutableBehavior
import akka.typed.scaladsl.{Actor, ActorContext}
import akka.typed.{ActorRef, Behavior}
import tmt.services.Command
import tmt.shared.engine.EngineBehavior._

import scala.collection.immutable.Queue

class EngineBehavior(ctx: ActorContext[EngineAction]) extends MutableBehavior[EngineAction] {

  var queue: Queue[Command]          = Queue.empty
  var afterPause: Queue[Command]     = Queue.empty
  var beforePause: Queue[Command]    = queue
  var ref: Option[ActorRef[Command]] = None
  var paused: Boolean                = false

  override def onMessage(msg: EngineAction): Behavior[EngineAction] = {
    msg match {
      case Push(x) if ref.isEmpty && !paused =>
        queue = queue.enqueue(x)
      case Push(x) if ref.isEmpty && paused =>
        queue = afterPause
        queue = queue.enqueue(x)
      case Push(x) if !paused =>
        ref.foreach(_ ! x)
        ref = None
      case Pull(replyTo) if hasNext =>
        val (elm, q) = queue.dequeue
        replyTo ! elm
        queue = q
      case Pull(replyTo) if paused && !hasNext =>
        ref = Some(replyTo)
      case Pull(replyTo) if !hasNext =>
        ref = Some(replyTo)
      case HasNext(replyTo) => replyTo ! hasNext
      case PauseAfter(step) =>
        paused = true
        afterPause = queue.drop(step)
        beforePause = queue.take(step)
        queue = beforePause
      case Resume =>
        paused = false
        queue = afterPause
        afterPause = Queue.empty
        beforePause = Queue.empty
        ref.foreach(x => ctx.self ! Pull(x))
        ref = None
      case Reset =>
        queue = Queue.empty
        beforePause = Queue.empty
        afterPause = Queue.empty
    }
    this
  }

  def hasNext: Boolean = queue.nonEmpty
}

object EngineBehavior {
  sealed trait EngineAction
  case class Push(command: Command)              extends EngineAction
  case class Pull(replyTo: ActorRef[Command])    extends EngineAction
  case class HasNext(replyTo: ActorRef[Boolean]) extends EngineAction
  case class PauseAfter(step: Int)               extends EngineAction
  case object Resume                             extends EngineAction
  case object Reset                              extends EngineAction

  def behavior: Behavior[EngineAction] = Actor.mutable(ctx => new EngineBehavior(ctx))
}
