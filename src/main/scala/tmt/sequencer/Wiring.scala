package tmt.sequencer

import akka.actor.ActorSystem
import akka.typed
import akka.typed.ActorRef
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import ammonite.sshd.SshdRepl
import tmt.services.LocationService

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring {
  implicit lazy val timeout: Timeout = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val engineActor: ActorRef[EngineBehaviour.Command] = Await.result(system.systemActorOf(EngineBehaviour.behaviour, "engine"), timeout.duration)
  lazy val engine = new Engine(engineActor, system)

  lazy val locationService = new LocationService(system)
  lazy val dsl = new Dsl(locationService)

  lazy val sshdRepl: SshdRepl = RemoteRepl.server(engine, dsl)
}
