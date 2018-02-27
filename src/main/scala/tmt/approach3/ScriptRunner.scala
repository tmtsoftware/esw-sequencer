package tmt.approach3

import ammonite.ops.Path
import tmt.sequencer.{CommandService, Engine}

import scala.util.control.Breaks
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ScriptRunner(engine: Engine, commandService: CommandService) {
  def run(path: Path): Future[Unit] = {
    val script = ScriptImports.load(path, commandService)

    val breaks = new Breaks

    Future {
      breaks.breakable {
        while (true) {
          val command = engine.pullNext()
          command.name match {
            case "shutdown"                    => script.onShutdown(); breaks.break()
            case x if x.startsWith("setup-")   => script.onSetup(command)
            case x if x.startsWith("observe-") => script.onObserve(command)
            case x                             => println("unknown command")
          }
        }
      }
    }
  }
}
