package tmt.sequencer

import akka.actor.{typed, ActorSystem}
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import ammonite.ops.Path

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring {
  implicit lazy val timeout: Timeout          = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val engineRef: ActorRef[EngineMsg] =
    Await.result(system.systemActorOf(EngineBehaviour.behavior, "engine"), timeout.duration)

  lazy val engine = new Engine(engineRef, system)

  lazy val locationService = new LocationService(system)
  lazy val commandService  = new CommandService(locationService, engine)(system.executionContext)

  def supervisorRef(path: Path): ActorRef[SupervisorMsg] = {
    val script           = ScriptImports.load(path, commandService)
    val supervisorFuture = system.systemActorOf(SupervisorBehavior.behavior(script, engineRef), "supervisor")
    Await.result(supervisorFuture, timeout.duration)
  }

  lazy val remoteRepl = new RemoteRepl(commandService, engine)
}
