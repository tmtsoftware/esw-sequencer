package tmt.approach1

import tmt.services.Command

object SequencerApp1 extends App {

  import tmt.sequencer.Dsl.wiring._

  sshdRepl.start()

  val params = if (args.isEmpty) Array("scripts/ocs-sequencer.sc") else args
  ammonite.Main.main0(params.toList, System.in, System.out, System.err)
}
