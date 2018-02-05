package tmt.approach1

import tmt.sequencer.EngineBehaviour.Push
import tmt.services.Command

object SequencerApp extends App {
  import tmt.sequencer.Dsl.wiring._

  sshdRepl.start()

  engineActor ! Push(Command("setup-assembly1", List(1, 2, 3)))
  engineActor ! Push(Command("setup-assembly2", List(10, 20, 30)))
  engineActor ! Push(Command("setup-assemblies-sequential", List(1, 2, 3, 10, 20, 30)))
  engineActor ! Push(Command("setup-assemblies-parallel", List(1, 2, 3, 10, 20, 30)))

  ammonite.Main.main(Array("scripts/ocs-sequencer.sc"))
}
