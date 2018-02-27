package tmt.approach3.sequencer

import tmt.sequencer.Engine
import tmt.services.Command

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

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
