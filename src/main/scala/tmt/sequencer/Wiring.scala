package tmt.sequencer

import akka.actor.ActorSystem
import akka.actor.typed
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import tmt.services.LocationService

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring {
  implicit lazy val timeout: Timeout          = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val engineActor: ActorRef[EngineBehaviour.EngineAction] =
    Await.result(system.systemActorOf(EngineBehaviour.behavior, "engine"), timeout.duration)
  lazy val engine = new Engine(engineActor, system)

  lazy val locationService = new LocationService(system)
  lazy val commandService  = new CommandService(locationService, engine)(system.executionContext)
  lazy val Command = tmt.services.Command
}
