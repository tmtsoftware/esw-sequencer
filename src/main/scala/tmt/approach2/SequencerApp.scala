package tmt.approach2

import tmt.services.Command

object SequencerApp extends App {
  import tmt.sequencer.Dsl.wiring._

  sshdRepl.start()

  ammonite.Main.main(Array("scripts/ocs-sequencer.sc"))
}
