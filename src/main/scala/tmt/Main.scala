package tmt

import tmt.sequencer.Engine.Push
import tmt.sequencer.Wiring

object Main extends App {
  val wiring = new Wiring
  import wiring._

  scriptRunner.run()
  sshdRepl.start()

  engine ! Push(1)
  engine ! Push(2)
  engine ! Push(3)
  engine ! Push(4)
  engine ! Push(5)
  engine ! Push(6)
}
