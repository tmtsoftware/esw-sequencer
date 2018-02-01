package tmt

import tmt.sequencer.Engine.Push
import tmt.sequencer.{RemoteRepl, ScriptRunner, Wiring}

object Main extends App {

  val engine = Wiring.engine

  new ScriptRunner(engine, Wiring.system).run()
  RemoteRepl.server.start()

  engine ! Push(1)
  engine ! Push(2)
  engine ! Push(3)
  engine ! Push(4)
  engine ! Push(5)
  engine ! Push(6)
}
