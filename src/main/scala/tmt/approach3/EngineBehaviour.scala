package tmt.approach3

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import tmt.approach3.EngineMsg._
import tmt.approach3.ScriptRunnerMsg.SequencerCommand
import tmt.services.Command

import scala.collection.immutable.Queue

class EngineBehaviour(ctx: ActorContext[EngineMsg]) extends MutableBehavior[EngineMsg] {

  var queue: Queue[Command]                   = Queue.empty
  var ref: Option[ActorRef[SequencerCommand]] = None
  var paused: Boolean                         = false

  override def onMessage(msg: EngineMsg): Behavior[EngineMsg] = {
    msg match {
      case Push(xs) if ref.isEmpty || paused =>
        queue = queue.enqueue(xs)
      case Push(xs) =>
        xs match {
          case head :: tail =>
            ref.foreach(_ ! SequencerCommand(head))
            ref = None
            queue = queue.enqueue(tail)
          case Nil => //No-Op
        }
      case Pull(replyTo) if hasNext =>
        val (elm, q) = queue.dequeue
        replyTo ! SequencerCommand(elm)
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
  def behavior: Behavior[EngineMsg] = Behaviors.mutable(ctx => new EngineBehaviour(ctx))
}
