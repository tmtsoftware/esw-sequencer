package tmt.approach2

import akka.typed.ActorSystem
import tmt.sequencer.Engine

import scala.concurrent.Future

class ScriptRunner(script: Script, engine: Engine, system: ActorSystem[_]) {
  import system.executionContext

  def run(): Unit = Future {
    while (true) {
      script.run(engine.pullNext())
    }
  }
}
