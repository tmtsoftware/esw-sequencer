package tmt.sequencer

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.core.{Engine, Sequencer, SequencerBehaviour, SupervisorBehavior}
import tmt.sequencer.db.{ScriptConfigs, ScriptRepo}
import tmt.sequencer.dsl.{Script, ScriptFactory}
import tmt.sequencer.gateway.{CswServices, LocationService}
import tmt.sequencer.models.{SequencerMsg, SupervisorMsg}

import scala.concurrent.duration.DurationDouble

class Wiring(sequencerId: String, observingMode: String, isProd: Boolean) {
  implicit lazy val timeout: Timeout           = Timeout(5.seconds)
  lazy implicit val system: ActorSystem        = ActorSystem("test")
  lazy implicit val materializer: Materializer = ActorMaterializer()

  lazy val scriptConfigs = new ScriptConfigs(system)
  lazy val repoDir: Path = if (isProd) Path(scriptConfigs.cloneDir) else ammonite.ops.pwd
  lazy val path: Path    = repoDir / RelPath(scriptConfigs.scriptFactoryPath)
  lazy val scriptRepo    = new ScriptRepo(scriptConfigs)

  lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior, "sequencer")
  lazy val sequencer                            = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService(system)
  lazy val cswServices     = new CswServices(locationService, sequencerId, observingMode)

  lazy val scriptFactory: ScriptFactory = ScriptImports.load(path)
  lazy val engine                       = new Engine(scriptFactory, cswServices, sequencerRef)

  lazy val supervisorRef: ActorRef[SupervisorMsg] = system.spawn(SupervisorBehavior.behavior(sequencerRef, engine), "supervisor")
  lazy val remoteRepl                             = new RemoteRepl(cswServices, sequencer, supervisorRef)
}
