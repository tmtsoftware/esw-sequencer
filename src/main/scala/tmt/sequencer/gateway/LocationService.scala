package tmt.sequencer.gateway

import akka.actor.ActorSystem
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.{ExecutionContext, Future}

class LocationService(actorSystem: ActorSystem) {
  def resolve(name: String): CommandService = {
    CommandService(AkkaLocation(name))
  }
}

case class AkkaLocation(name: String)

case class CommandService(assemblyLoc: AkkaLocation) {
  def submit(command: Command)(implicit ec: ExecutionContext): Future[CommandResult] = Future {
    Thread.sleep(10000)
    CommandResult.Success(s"\nResult submit: [${assemblyLoc.name}] - $command")
  }

  def submitAndSubscribe(command: Command)(implicit ec: ExecutionContext): Future[CommandResult] = Future {
    CommandResult.Success(s"\nResult submit and subscribe: [${assemblyLoc.name}] - $command")
  }
}
