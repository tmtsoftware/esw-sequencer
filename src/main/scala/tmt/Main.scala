package tmt

import akka.NotUsed
import akka.actor.ActorSystem
import akka.typed.Behavior
import akka.typed.scaladsl.Actor
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout
import sequencer.Engine.Push
import sequencer.{Engine, ScriptRunner}

import scala.concurrent.duration.DurationLong

object Main extends App {
  private val system = ActorSystem("test").toTyped
  implicit val timeout: Timeout = Timeout(5.seconds)
  system.systemActorOf(Simulation.behaviour, "simulation")
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
