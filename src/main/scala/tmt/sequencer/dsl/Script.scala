package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{Command, CommandResults}

import scala.collection.mutable
import scala.concurrent.Future
import scala.language.implicitConversions

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[CommandResults]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[CommandResults]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[CommandResults]])(_ orElse _)

  def execute(command: Command): Future[CommandResults] = combinedHandler.lift(command).getOrElse {
    println(s"unknown command=$command")
    spawn(CommandResults.empty)
  }

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def processNext(): Future[Unit] = cs.processNext(this)

  protected def handleCommand(name: String)(handler: Command => Future[CommandResults]): Unit = commandHandlers += {
    case command if command.name == name => handler(command)
  }

  protected def onShutdown(): Future[Done] = spawn(Done)
}
