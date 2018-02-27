package tmt.approach2

import java.io.File

import tmt.sequencer.RemoteRepl

object SequencerApp2 extends App {

  import tmt.sequencer.Dsl._

  init()

  RemoteRepl.server.start()

  engine.pushAll(List(Command("setup-assemblies-parallel", List(1, 2, 3, 10, 20, 30))))

  val params = if (args.isEmpty) Array("scripts/script1.sc") else args
  ScriptLoader.fromFile(new File(params(0))).run()
}
