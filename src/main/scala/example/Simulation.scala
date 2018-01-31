package example

import akka.NotUsed
import akka.typed.Behavior
import akka.typed.scaladsl.Actor
import example.Sequencer.Push

object Simulation {
  val behaviour: Behavior[NotUsed] = Actor.deferred { ctx =>
    val actorRef = ctx.spawn(Sequencer.behaviour, "sequencer")
    val simulator = new ScriptLike(actorRef, ctx)
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
