package tmt.approach3

import akka.actor.typed.scaladsl.Behaviors.MutableBehavior
import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.{ActorRef, Behavior}
import ammonite.ops.Path
import tmt.approach3.ScriptRunnerBehavior.{ControlCommand, ScriptRunnerMsg, SequencerCommand, SequencerEvent}
import tmt.sequencer.CommandService
import tmt.sequencer.EngineBehaviour.{EngineMsg, Pull}
import tmt.services.Command

import scala.concurrent.Future
import scala.util.{Failure, Success}

class ScriptRunnerBehavior(script: Script, engineRef: ActorRef[EngineMsg], ctx: ActorContext[ScriptRunnerMsg])
    extends MutableBehavior[ScriptRunnerMsg] {

  import ctx.executionContext

  engineRef ! Pull(ctx.self)

  override def onMessage(msg: ScriptRunnerMsg): Behavior[ScriptRunnerMsg] = {
    msg match {
      case SequencerCommand(command) =>
        def run(): Unit = command.name match {
          case x if x.startsWith("setup-")   => script.onSetup(command)
          case x if x.startsWith("observe-") => script.onObserve(command)
          case x                             => println("unknown command")
        }
        Future(concurrent.blocking(run())).onComplete {
          case Success(value) => engineRef ! Pull(ctx.self)
          case Failure(ex)    =>
        }
      case ControlCommand("shutdown") =>
        Future(concurrent.blocking(script.onShutdown())).onComplete {
          case Success(value) =>
          case Failure(ex)    =>
        }
      case msg: SequencerEvent =>
        Future(concurrent.blocking(script.onEvent(msg))).onComplete {
          case Success(value) =>
          case Failure(ex)    =>
        }
      case _ => println(s"unknown sequencer msg=$msg")
    }
    this
  }
}

object ScriptRunnerBehavior {
  sealed trait ScriptRunnerMsg
  case class SequencerCommand(command: Command) extends ScriptRunnerMsg
  case class ControlCommand(name: String)       extends ScriptRunnerMsg
  case class SequencerEvent(value: String)      extends ScriptRunnerMsg
}

class ScriptRunnerBehaviorFactory(commandService: CommandService, engineRef: ActorRef[EngineMsg]) {
  def behavior(path: Path): Behavior[ScriptRunnerMsg] = {
    val script = ScriptImports.load(path, commandService)
    Behaviors.mutable(ctx => new ScriptRunnerBehavior(script, engineRef, ctx))
  }
}
