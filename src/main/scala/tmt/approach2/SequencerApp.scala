package tmt.approach2

import java.io.File

import tmt.sequencer.EngineBehaviour.Push

object SequencerApp extends App {
  import tmt.sequencer.ScriptImports.wiring._

  sshdRepl.start()

  engineActor ! Push(1)
  engineActor ! Push(2)
  engineActor ! Push(3)
  engineActor ! Push(4)
  engineActor ! Push(5)
  engineActor ! Push(6)

  ScriptLoader.fromFile(new File("scripts/ocs-sequencer.sc")).run()
}
