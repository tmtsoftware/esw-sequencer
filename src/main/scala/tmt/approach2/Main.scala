package tmt.approach2

import tmt.sequencer.EngineBehaviour.Push

object Main extends App {
  val wiring = new Approach2Wiring("scripts/approach2/ocs-sequencer.ss")
  import wiring._

  scriptRunner.run()
  sshdRepl.start()

  engineActor ! Push(1)
  engineActor ! Push(2)
  engineActor ! Push(3)
  engineActor ! Push(4)
  engineActor ! Push(5)
  engineActor ! Push(6)
}
