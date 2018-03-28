package tmt.sequencer

import akka.actor.typed.ActorSystem
import tmt.sequencer.models.{Command, CommandResult}

import scala.concurrent.{ExecutionContext, Future}

class LocationService(actorSystem: ActorSystem[_]) {
  def resolve(name: String): CommandService = {
    CommandService(AkkaLocation(name))
  }
}

case class AkkaLocation(name: String)

case class CommandService(assemblyLoc: AkkaLocation) {
  def submit(command: Command)(implicit ec: ExecutionContext): Future[CommandResult] = Future {
    println(s"\nCommand received submit: [${assemblyLoc.name}] - $command")
    CommandResult.Success(s"\nResult submit: [${assemblyLoc.name}] - $command")
  }

  def submitAndSubscribe(command: Command)(implicit ec: ExecutionContext): Future[CommandResult] = Future {
    println(s"\nCommand received submit and subscribe: [${assemblyLoc.name}] - $command")
    CommandResult.Success(s"\nResult submit and subscribe: [${assemblyLoc.name}] - $command")
  }
}
