package tmt.sequencer

import java.io.File

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
  implicit val timeout: Timeout = Timeout(5.seconds)
  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped
  lazy val engine: ActorRef[Engine.Command] = Await.result(system.systemActorOf(Engine.behaviour, "engine"), timeout.duration)

  lazy val locationService = new LocationService(system)
  lazy val dsl = new Dsl(locationService)
  lazy val sshdRepl: SshdRepl = RemoteRepl.server(engine, dsl)
  lazy val engineAdapter = new EngineAdapter(engine, system)
}

class Approach1Wiring(scriptFilePath: String) extends Wiring {
  //approach-1
  lazy val scriptFactory: ScriptFactory = ScriptFactory.fromFile(new File(scriptFilePath))
  lazy val script: Script = scriptFactory.make(dsl)
  lazy val scriptRunner = new ScriptRunner(script, engineAdapter, system)
}
