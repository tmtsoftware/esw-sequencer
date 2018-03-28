package tmt.sequencer

import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.{typed, ActorSystem}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}
import tmt.sequencer.core.{Engine, Sequencer, SequencerBehaviour, SupervisorBehavior}
import tmt.sequencer.db.{ScriptConfigs, ScriptRepo}
import tmt.sequencer.dsl.Script
import tmt.sequencer.gateway.{CswServices, LocationService}

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring(scriptFile: String, isProd: Boolean) {
  implicit lazy val timeout: Timeout                = Timeout(5.seconds)
  lazy implicit val system: ActorSystem             = ActorSystem("test")
  lazy implicit val materializer: ActorMaterializer = ActorMaterializer()

  lazy val scriptConfigs = new ScriptConfigs(system)
  lazy val repoDir: Path = if (isProd) Path(scriptConfigs.cloneDir) else ammonite.ops.pwd
  lazy val path: Path    = repoDir / RelPath(scriptFile)

  lazy val scriptRepo = new ScriptRepo(scriptConfigs)

  lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior, "sequencer")

  lazy val sequencer = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService(system)

  lazy val commandService = new CswServices(locationService)

  lazy val script: Script = ScriptImports.load(path, commandService)

  lazy val engine = new Engine(script, sequencerRef, system)

  lazy val supervisorRef: ActorRef[SupervisorMsg] =
    system.spawn(SupervisorBehavior.behavior(script, sequencerRef, engine), "supervisor")

  lazy val remoteRepl = new RemoteRepl(commandService, sequencer, supervisorRef)
}
