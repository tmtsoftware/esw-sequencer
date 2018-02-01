package tmt

import akka.NotUsed
import akka.actor.ActorSystem
import akka.typed.Behavior
import akka.typed.scaladsl.Actor
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import sequencer.Engine.Push
import sequencer.{Engine, ScriptRunner}

import scala.concurrent.duration.DurationLong

object Main extends App {
  private val system = ActorSystem("test").toTyped
  implicit val timeout: Timeout = Timeout(5.seconds)
  system.systemActorOf(Simulation.behaviour, "simulation")

  import ammonite.sshd._
  val replServer = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef =
      """
        |repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix
        |println("Starting Debugging!")
        |import tmt.sequencer.Dsl
        |val dsl: Dsl = Dsl.build()
        |import dsl._
      """.stripMargin
  )
  replServer.start()
}

object Simulation {
  lazy val behaviour: Behavior[NotUsed] = Actor.deferred { ctx =>
    val engine = ctx.spawn(Engine.behaviour, "sequencer")
    val runner = new ScriptRunner(engine, ctx)
    runner.run()

    engine ! Push(1)
    engine ! Push(2)
    engine ! Push(3)
    engine ! Push(4)
    engine ! Push(5)
    engine ! Push(6)

    //    Thread.sleep(5000)
    Actor.empty
  }
}
