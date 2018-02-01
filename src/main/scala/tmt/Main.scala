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
    val actorRef = ctx.spawn(Engine.behaviour, "sequencer")
    val simulator = new ScriptRunner(actorRef, ctx)
    simulator.run()

    actorRef ! Push(1)
    actorRef ! Push(2)
    actorRef ! Push(3)
    actorRef ! Push(4)
    actorRef ! Push(5)
    actorRef ! Push(6)

    //    Thread.sleep(5000)
    Actor.empty
  }
}
