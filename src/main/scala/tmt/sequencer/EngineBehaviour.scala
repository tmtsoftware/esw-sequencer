package tmt.sequencer

import akka.typed.scaladsl.Actor
import akka.typed.{ActorRef, Behavior}
import akka.typed.scaladsl.Actor.MutableBehavior
import tmt.sequencer.EngineBehaviour.{Command, Pull, Push, Value}

import scala.collection.immutable.Queue

class EngineBehaviour extends MutableBehavior[Command] {

  var queue: Queue[Int] = Queue.empty
  var ref: Option[ActorRef[Value]] = None

  override def onMessage(msg: Command): Behavior[Command] = {
    msg match {
      case Push(x) if ref.isEmpty      =>
        queue = queue.enqueue(x)
      case Push(x) =>
        ref.foreach(_ ! Value(x))
        ref = None
      case Pull(replyTo) if queue.nonEmpty =>
        val (elm, q) = queue.dequeue
        replyTo ! Value(elm)
        queue = q
      case Pull(replyTo) =>
        ref = Some(replyTo)
    }
    this
  }
}

object EngineBehaviour {
  sealed trait Command
  case class Push(x: Int) extends Command
  case class Pull(replyTo: ActorRef[Value]) extends Command

  case class Value(x: Int)

  def behaviour: Behavior[Command] = Actor.mutable(ctx => new EngineBehaviour)
}
