package tmt.approach1

import tmt.sequencer.RemoteRepl

object SequencerApp1 extends App {

  import tmt.sequencer.Dsl._

  init()

  RemoteRepl.server.start()

  engine.pushAll(List(Command("setup-assemblies-parallel", List(1, 2, 3, 10, 20, 30))))

  val params = if (args.isEmpty) Array("scripts/script1.sc") else args
  ammonite.Main.main0(params.toList, System.in, System.out, System.err)

  println("sequencer script loaded and running")

  //Run script2 which internally calls teardown of script1
  //Call script1 function from program
  ammonite.Main.main0(List("scripts/script2.sc"), System.in, System.out, System.err)

}
