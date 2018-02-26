package tmt.approach1

import tmt.sequencer.RemoteRepl

object Demo1 extends App {

  import tmt.sequencer.Dsl._

  init()

  RemoteRepl.server.start()

  val params = if (args.isEmpty) Array("scripts/dd.sc") else args
  ammonite.Main.main0(params.toList, System.in, System.out, System.err)
}
