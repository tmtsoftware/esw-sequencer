package tmt.sequencer

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import ammonite.ops.{Path, RelPath}
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}
import tmt.sequencer.git.{ScriptConfigs, ScriptRepo}
import tmt.sequencer.dsl.{CswServices, Script}
import tmt.sequencer.gateway.LocationService
import tmt.sequencer.messages.{SequencerMsg, SupervisorMsg}
import tmt.sequencer.rpc.server._
import tmt.sequencer.rpc.server.spike.Streaming2Impl

import scala.concurrent.duration.DurationDouble

class Wiring(sequencerId: String, observingMode: String, port: Option[Int]) {
  implicit lazy val timeout: Timeout           = Timeout(5.seconds)
  lazy implicit val system: ActorSystem        = ActorSystem("test")
  lazy implicit val materializer: Materializer = ActorMaterializer()
  import system.dispatcher

  lazy val scriptConfigs = new ScriptConfigs(system)
  lazy val scriptRepo    = new ScriptRepo(scriptConfigs, locationService)

  lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior, "sequencer")
  lazy val sequencer                            = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService
  lazy val engine          = new Engine
  lazy val cswServices     = new CswServices(sequencer, engine, locationService, sequencerId, observingMode)

  lazy val script: Script = ScriptImports.load().get(cswServices)

  lazy val sequenceEditor: SequenceEditor = new SequenceEditorImpl(sequencerRef, script)
  lazy val sequenceFeeder: SequenceFeeder = new SequenceFeederImpl(sequencerRef)
  lazy val routes                         = new Routes(sequenceFeeder, sequenceEditor)
  lazy val routes2                        = new Routes2(sequenceFeeder, sequenceEditor, Streaming2Impl)
  lazy val rpcConfigs                     = new RpcConfigs(port)
  lazy val rpcServer                      = new RpcServer(rpcConfigs, routes)
  lazy val rpcServer2                     = new RpcServer2(rpcConfigs, routes2)

  lazy val supervisorRef: ActorRef[SupervisorMsg] = system.spawn(SupervisorBehavior.behavior(sequencerRef, script), "supervisor")
  lazy val remoteRepl                             = new RemoteRepl(cswServices, sequencer, supervisorRef, sequenceFeeder, sequenceEditor, rpcConfigs)
}
