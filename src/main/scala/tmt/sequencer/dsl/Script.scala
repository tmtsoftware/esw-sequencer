package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{Command, CommandResponse}

import scala.collection.mutable
import scala.concurrent.Future
import scala.language.implicitConversions

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[Set[CommandResponse]]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[Set[CommandResponse]]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[Set[CommandResponse]]])(_ orElse _)

  def execute(command: Command): Future[CommandResponse] = spawn {
    val responses = combinedHandler
      .lift(command)
      .getOrElse {
        println(s"unknown command=$command")
        spawn(Set.empty)
      }
      .await
    CommandResponse.Composite(command.id, responses)
  }

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def handleCommand(name: String)(handler: Command => Future[Set[CommandResponse]]): Unit = commandHandlers += {
    case command if command.name == name => handler(command)
  }

  protected def onShutdown(): Future[Done] = spawn(Done)
}
