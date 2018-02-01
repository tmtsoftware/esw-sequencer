package tmt.sequencer

import akka.actor.ActorSystem
import akka.typed
import akka.typed.ActorRef
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

object Wiring {
  implicit val timeout: Timeout = Timeout(5.seconds)

  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped
  lazy val engine: ActorRef[Engine.Command] = Await.result(
    system.systemActorOf(Engine.behaviour, "engine"),
    timeout.duration
  )
}
