package tmt.approach3

import tmt.sequencer.Engine
import scala.util.control.Breaks

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class ScriptRunner(engine: Engine) {
  def run(script: Script): Future[Unit] = {
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
