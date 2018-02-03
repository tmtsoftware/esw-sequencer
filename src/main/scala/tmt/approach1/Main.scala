package tmt.approach1

import tmt.sequencer.EngineBehaviour.Push

object Main extends App {
  import ScriptImports.wiring._

  sshdRepl.start()

  engineActor ! Push(1)
  engineActor ! Push(2)
  engineActor ! Push(3)
  engineActor ! Push(4)
  engineActor ! Push(5)
  engineActor ! Push(6)

  ammonite.Main.main(Array("scripts/ocs-sequencer.sc"))
}
