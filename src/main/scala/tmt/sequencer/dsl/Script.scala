package tmt.sequencer.dsl

import akka.Done
import tmt.sequencer.models.{AggregateResponse, Command}

import scala.concurrent.Future

abstract class Script(cs: CswServices) extends ActiveObject {
  private lazy val commandHandler: Command => Future[AggregateResponse] = cs.commandHandlerBuilder.build { input =>
    println(s"unknown command=$input")
    spawn(AggregateResponse)
  }

  def execute(command: Command): Future[AggregateResponse] = spawn(commandHandler(command).await)

  def shutdown(): Future[Done] = onShutdown().map(_ => shutdownEc())

  protected def onShutdown(): Future[Done] = spawn(Done)
}
