package example

import akka.actor.ActorSystem
import akka.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.util.Timeout

import scala.concurrent.duration.DurationLong

object Main extends App {
  private val system = ActorSystem("test").toTyped
  implicit val timeout: Timeout = Timeout(5.seconds)
  system.systemActorOf(Simulation.behaviour, "simulation")
}
