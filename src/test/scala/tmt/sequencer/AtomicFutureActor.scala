package tmt.sequencer

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import scala.concurrent.Future

object AtomicFutureActor {
  val behavior: Behavior[FutureActorMsg] = Behaviors.setup { ctx =>
    var x              = 0
    var wasIncremented = false

    import ctx.executionContext

    def change(delta: Int): Unit = {
      if (wasIncremented) {
        x = x - delta
        wasIncremented = false
      } else {
        x = x + delta
        wasIncremented = true
      }
    }
    Behaviors.immutable[FutureActorMsg] {
      case (_, msg) =>
        msg match {
          case ActorMsg              => change(1)
          case FutureMsg             => Future(1).foreach(delta => ctx.self ! FutureComplete(delta))
          case FutureComplete(delta) => change(delta)
          case GetX(replyTo)         => replyTo ! x
        }
        Behaviors.same
    }
  }

  sealed trait FutureActorMsg
  case object ActorMsg                    extends FutureActorMsg
  case object FutureMsg                   extends FutureActorMsg
  case class FutureComplete(delta: Int)   extends FutureActorMsg
  case class GetX(replyTo: ActorRef[Int]) extends FutureActorMsg
}
