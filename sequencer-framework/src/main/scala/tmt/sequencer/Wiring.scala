package tmt.sequencer

import akka.actor.ActorSystem
import akka.actor.typed.ActorRef
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.Timeout
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}
import tmt.sequencer.config.ScriptConfigs
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

  lazy val sequencerRef: ActorRef[SequencerMsg] = system.spawn(SequencerBehaviour.behavior, "sequencer")
  lazy val sequencer                            = new Sequencer(sequencerRef, system)

  lazy val locationService = new LocationService
  lazy val engine          = new Engine

  lazy val scriptConfigs = new ScriptConfigs(system)

  lazy val canonicalPath: String = Try(scriptName.getOrElse(scriptConfigs.scriptCanonicalPath)).toOption
    .getOrElse(
      throw new RuntimeException("Please provide script name either through command line or configuration settings")
    )

  lazy val clazz: Class[_] = load(canonicalPath)
  lazy val cswServices     = new CswServices(sequencer, engine, locationService, clazz.getSimpleName)

  lazy val script = load(clazz, cswServices)

  lazy val sequenceEditor: SequenceEditor = new SequenceEditorImpl(sequencerRef, script)
  lazy val sequenceFeeder: SequenceFeeder = new SequenceFeederImpl(sequencerRef)
  lazy val routes                         = new Routes(sequenceFeeder, sequenceEditor)
  lazy val routes2                        = new Routes2(sequenceFeeder, sequenceEditor, Streaming2Impl)
  lazy val rpcConfigs                     = new RpcConfigs(port)
  lazy val rpcServer                      = new RpcServer(rpcConfigs, routes)
  lazy val rpcServer2                     = new RpcServer2(rpcConfigs, routes2)

  lazy val remoteRepl = new RemoteRepl(cswServices, sequencer, sequenceFeeder, sequenceEditor, rpcConfigs)

  private[tmt] def load(canonicalPath: String): Class[_] = {
    getClass.getClassLoader
      .loadClass(canonicalPath)
  }

  private[tmt] def load(clazz: Class[_], cswServices: CswServices): Script = {
    clazz.getConstructor(classOf[CswServices]).newInstance(cswServices).asInstanceOf[Script]
  }
  lazy val supervisorRef: ActorRef[SupervisorMsg] = system.spawn(SupervisorBehavior.behavior(sequencerRef, script), "supervisor")
  lazy val remoteRepl                             = new RemoteRepl(cswServices, sequencer, supervisorRef, sequenceFeeder, sequenceEditor, rpcConfigs)
}
