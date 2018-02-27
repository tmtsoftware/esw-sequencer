package tmt.approach3

import tmt.sequencer.Engine
import tmt.services.Command

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait ControlDsl {
  def engine: Engine

  def forEach(f: Command => Unit): Unit = {
    loop(f(engine.pullNext()))
  }

  def loop(block: => Unit): Unit = Future {
    while (true) {
      block
    }
  }
}
