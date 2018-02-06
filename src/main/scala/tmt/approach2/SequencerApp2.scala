package tmt.approach2

import java.io.File

import tmt.services.Command

object SequencerApp2 extends App {
  import tmt.sequencer.Dsl.wiring._

  sshdRepl.start()

  val params = if (args.isEmpty) Array("scripts/ocs-sequencer.sc") else args
  ScriptLoader.fromFile(new File(params(0))).run()
}
