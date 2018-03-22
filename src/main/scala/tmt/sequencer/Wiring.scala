package tmt.sequencer

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{typed, ActorSystem}
import akka.util.Timeout
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.models.{EngineMsg, SequencerMsg, SupervisorMsg}
import tmt.sequencer.util.ScriptRepo

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring(scriptFile: String, isProd: Boolean) {
  implicit lazy val timeout: Timeout          = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val scriptConfigs = new ScriptConfigs(system)
  lazy val repoDir: Path = if (isProd) Path(scriptConfigs.cloneDir) else ammonite.ops.pwd
  lazy val path: Path    = repoDir / RelPath(scriptFile)

  lazy val scriptRepo = new ScriptRepo(scriptConfigs)

  lazy val sequencerRef: ActorRef[SequencerMsg] =
    Await.result(system.systemActorOf(SequencerBehaviour.behavior, "sequencer"), timeout.duration)

  lazy val sequencer = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService(system)

  lazy val commandService = new CswServices(locationService, engineRef)(system.executionContext)

  lazy val script: Script = ScriptImports.load(path, commandService)

  lazy val engineRef: ActorRef[EngineMsg] =
    Await.result(system.systemActorOf(EngineBehavior.behavior(script, sequencerRef), "engine"), timeout.duration)

  lazy val supervisorRef: ActorRef[SupervisorMsg] =
    Await.result(system.systemActorOf(SupervisorBehavior.behavior(script, sequencerRef, engineRef), "supervisor"),
                 timeout.duration)

  lazy val remoteRepl = new RemoteRepl(commandService, sequencer, supervisorRef)
}
