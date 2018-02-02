package tmt.sequencer

import akka.actor.ActorSystem
import akka.typed
import akka.typed.ActorRef
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import ammonite.sshd.SshdRepl

import scala.concurrent.Await
import scala.concurrent.duration.DurationDouble

class Wiring {
  implicit val timeout: Timeout = Timeout(5.seconds)

  lazy val system: typed.ActorSystem[Nothing] = ActorSystem("test").toTyped

  lazy val engine: ActorRef[Engine.Command] = Await.result(
    system.systemActorOf(Engine.behaviour, "engine"),
    timeout.duration
  )

  lazy val scriptRunner = new ScriptRunner(engine, system)
  lazy val sshdRepl: SshdRepl = RemoteRepl.server(engine)
}
