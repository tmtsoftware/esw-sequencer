package tmt.sequencer

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{typed, ActorSystem}
import akka.util.Timeout
import ammonite.ops.Path
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring(path: Path) {
  implicit lazy val timeout: Timeout          = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val sequencerRef: ActorRef[SequencerMsg] =
    Await.result(system.systemActorOf(SequencerBehaviour.behavior, "sequencer"), timeout.duration)

  lazy val sequencer = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService(system)
  lazy val commandService  = new CswServices(locationService, sequencer)(system.executionContext)

  lazy val script: Script = ScriptImports.load(path, commandService)

  lazy val supervisorRef: ActorRef[SupervisorMsg] =
    Await.result(system.systemActorOf(SupervisorBehavior.behavior(script, sequencerRef), "supervisor"), timeout.duration)

  lazy val remoteRepl = new RemoteRepl(commandService, sequencer, supervisorRef)
}
