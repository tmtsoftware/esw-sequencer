package tmt.sequencer.dsl

import tmt.sequencer.gateway.CswServices
import tmt.sequencer.models.{Command, CommandResults}

import scala.collection.mutable
import scala.concurrent.Future
import scala.concurrent.duration.DurationDouble
import scala.language.implicitConversions

abstract class Script(cs: CswServices) extends ActiveObject {
  private var commandHandlers: mutable.Buffer[PartialFunction[Command, Future[CommandResults]]] = mutable.Buffer.empty

  private def combinedHandler: PartialFunction[Command, Future[CommandResults]] =
    commandHandlers.foldLeft(PartialFunction.empty[Command, Future[CommandResults]])(_ orElse _)

  def execute(command: Command): Future[CommandResults] = combinedHandler.lift(command).getOrElse {
    println(s"unknown command=$command")
    spawn(CommandResults.empty)
  }

  def shutdown(): Future[Unit] = onShutdown().map(_ => shutdownEc())

  protected def processNext(): Future[Unit] = cs.processNext(this)

  protected def handleCommand(name: String)(f: Command => Future[CommandResults]): Unit = commandHandlers += {
    case command if command.name == name => f(command)
  }

  protected def onShutdown(): Future[Unit] = spawn(())
}
