package tmt.approach1

import tmt.sequencer.RemoteRepl

object Demo1 extends App {

  import tmt.sequencer.Dsl._

  init()

  RemoteRepl.server.start()

  val params = if (args.isEmpty) Array("scripts/eee.sc") else args
  ammonite.Main.main0(params.toList, System.in, System.out, System.err)

  println("Calling script function")

  Thread.sleep(2000)
//  Dirty.x.square(22)
}
