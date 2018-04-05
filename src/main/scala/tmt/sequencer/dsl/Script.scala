package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{Command, CommandResult}

import scala.collection.mutable
import scala.concurrent.Future
import scala.language.implicitConversions

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[CommandResult]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[CommandResult]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[CommandResult]])(_ orElse _)

  def execute(command: Command): Future[CommandResult] = combinedHandler.lift(command).getOrElse {
    println(s"unknown command=$command")
    spawn(CommandResult.Empty(command.id))
  }

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def processNext(): Future[Done] = cs.processNext(this)

  protected def handleCommand(name: String)(handler: Command => Future[CommandResult]): Unit = commandHandlers += {
    case command if command.name == name => handler(command)
  }

  protected def onShutdown(): Future[Done] = spawn(Done)
}
