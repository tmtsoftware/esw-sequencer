package tmt.sequencer

import akka.actor.typed.ActorSystem
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.{ExecutionContext, Future}

class LocationService(actorSystem: ActorSystem[_]) {
  def resolve(name: String): ComponentRef = ComponentRef(name)
}

case class ComponentRef(name: String) {
  def setup(command: Command)(implicit ec: ExecutionContext): Future[CommandResult] = Future {
    println(s"\nCommand received: [$name] - $command")
    CommandResult.Single(s"Result: [$name] - $command")
  }
}
