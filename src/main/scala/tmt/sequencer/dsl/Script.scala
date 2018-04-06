package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{AggregateResponse, Command, CommandResponse}

import scala.collection.mutable
import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[AggregateResponse]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[AggregateResponse]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[AggregateResponse]])(_ orElse _)

  private[sequencer] def execute(command: Command): Future[AggregateResponse] = spawn {
    combinedHandler
      .lift(command)
      .getOrElse {
        println(s"unknown command=$command")
        spawn(AggregateResponse(Set.empty))
      }
      .await
  }

  def executeToBeDeleted(command: Command): Future[Set[CommandResponse]] = spawn {
    execute(command).await.responses
  }

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def handleCommand(name: String)(handler: Command => Future[AggregateResponse]): Unit = commandHandlers += {
    case command if command.name == name => handler(command)
  }

  protected def onShutdown(): Future[Done] = spawn(Done)
}
