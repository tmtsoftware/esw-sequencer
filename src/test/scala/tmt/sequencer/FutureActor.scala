package tmt.sequencer

import akka.actor.typed.{ActorRef, Behavior}
import akka.actor.typed.scaladsl.Behaviors

import scala.concurrent.Future

object FutureActor {
  val behavior: Behavior[FutureActorMsg] = Behaviors.setup { ctx =>
    var x = 0
    import ctx.executionContext

    Behaviors.immutable[FutureActorMsg] {
      case (_, msg) =>
        msg match {
          case ActorMsg      => x = x + 1
          case FutureMsg     => Future(x = x - 1)
          case GetX(replyTo) => replyTo ! x
        }
        Behaviors.same
    }
  }

  sealed trait FutureActorMsg
  case object ActorMsg                    extends FutureActorMsg
  case object FutureMsg                   extends FutureActorMsg
  case class GetX(replyTo: ActorRef[Int]) extends FutureActorMsg
}
