package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.sequencer.EngineBehaviour._
import tmt.services.Command

import scala.collection.immutable.Queue

class EngineBehaviour(ctx: ActorContext[EngineAction]) extends MutableBehavior[EngineAction] {

  var executingQueue: Queue[Command]   = Queue.empty
  var queueAfterPause: Queue[Command]  = Queue.empty
  var queueBeforePause: Queue[Command] = executingQueue
  var ref: Option[ActorRef[Command]]   = None
  var paused: Boolean                  = false

  override def onMessage(msg: EngineAction): Behavior[EngineAction] = {
    msg match {
      case Push(x) if paused =>
        queueAfterPause = queueAfterPause.enqueue(x)
      case Push(x) if ref.isEmpty =>
        executingQueue = executingQueue.enqueue(x)
      case Push(x) =>
        ref.foreach(_ ! x)
        ref = None
      case Pull(replyTo) if hasNext =>
        val (elm, q) = executingQueue.dequeue
        replyTo ! elm
        executingQueue = q
      case Pull(replyTo) =>
        ref = Some(replyTo)
      case HasNext(replyTo) => replyTo ! hasNext
      case PauseAfter(step) =>
        paused = true
        queueAfterPause = executingQueue.drop(step)
        queueBeforePause = executingQueue.take(step)
        executingQueue = queueBeforePause
      case Resume =>
        paused = false
        executingQueue = executingQueue ++ queueAfterPause
        queueAfterPause = Queue.empty
        queueBeforePause = Queue.empty
        ref.foreach(x => ctx.self ! Pull(x))
        ref = None
      case Reset =>
        executingQueue = Queue.empty
        queueBeforePause = Queue.empty
        queueAfterPause = Queue.empty
    }
    this
  }

  def hasNext: Boolean = executingQueue.nonEmpty
}

object EngineBehaviour {
  sealed trait EngineAction
  case class Push(command: Command)              extends EngineAction
  case class Pull(replyTo: ActorRef[Command])    extends EngineAction
  case class HasNext(replyTo: ActorRef[Boolean]) extends EngineAction
  case class PauseAfter(step: Int)               extends EngineAction
  case object Resume                             extends EngineAction
  case object Reset                              extends EngineAction

  def behavior: Behavior[EngineAction] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
