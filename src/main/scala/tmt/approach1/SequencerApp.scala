package tmt.approach1

import java.io.File

import tmt.services.Command

object SequencerApp extends App {
  import tmt.sequencer.Dsl.wiring._

  sshdRepl.start()

  ScriptLoader.fromFile(new File("scripts/ocs-sequencer.sc")).run()
}
