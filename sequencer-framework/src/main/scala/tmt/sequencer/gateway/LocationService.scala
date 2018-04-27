package tmt.sequencer.gateway

import tmt.sequencer.models.{Command, CommandResponse}

import scala.concurrent.{ExecutionContext, Future}

class LocationService {

  def commandService(name: String): CommandService = CommandService(AkkaLocation(name))

  def sequenceProcessorUri(sequencerScriptName: String): String = {
    val port = (sequencerScriptName) match {
      case "IrisDarkNight"  => 8000
      case "IrisClearSkies" => 8001
      case "TcsDarkNight"   => 7000
      case "TcsClearSkies"  => 7001
      case _                => throw new RuntimeException(s"can not locate sequencer for script= $sequencerScriptName")
    }
    s"http://0.0.0.0:$port"
  }

}

case class AkkaLocation(name: String)

case class CommandService(assemblyLoc: AkkaLocation) {
  def submit(command: Command)(implicit ec: ExecutionContext): Future[CommandResponse] = Future {
    Thread.sleep(2000)
    CommandResponse.Success(command.id, s"Result submit: [${assemblyLoc.name}] - $command")
  }

  def submitAndSubscribe(command: Command)(implicit ec: ExecutionContext): Future[CommandResponse] = Future {
    CommandResponse.Success(command.id, s"Result submit and subscribe: [${assemblyLoc.name}] - $command")
  }
}
