package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{AggregateResponse, Command}

import scala.collection.mutable
import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[AggregateResponse]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[AggregateResponse]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[AggregateResponse]])(_ orElse _)

  def execute(command: Command): Future[AggregateResponse] = spawn {
    combinedHandler
      .lift(command)
      .getOrElse {
        println(s"unknown command=$command")
        spawn(AggregateResponse)
      }
      .await
  }

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def handleCommand(name: String)(handler: Command => Future[AggregateResponse]): Unit = commandHandlers += {
    case command if command.name == name => handler(command)
  }

  protected def onShutdown(): Future[Done] = spawn(Done)
}
