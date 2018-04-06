package tmt.sequencer.gateway

import akka.actor.ActorSystem
import tmt.sequencer.models.{Command, CommandResponse}

import scala.concurrent.{ExecutionContext, Future}

class LocationService(actorSystem: ActorSystem) {
  def resolve(name: String): CommandService = {
    CommandService(AkkaLocation(name))
  }
}

case class AkkaLocation(name: String)

case class CommandService(assemblyLoc: AkkaLocation) {
  def submit(command: Command)(implicit ec: ExecutionContext): Future[CommandResponse] = Future {
    Thread.sleep(10000)
    CommandResponse.Success(command.id, command.parentId, s"Result submit: [${assemblyLoc.name}] - $command")
  }

  def submitAndSubscribe(command: Command)(implicit ec: ExecutionContext): Future[CommandResponse] = Future {
    CommandResponse.Success(command.id, command.parentId, s"Result submit and subscribe: [${assemblyLoc.name}] - $command")
  }
}
