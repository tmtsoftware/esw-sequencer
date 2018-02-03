package tmt.approach2

import java.io.File

object ScriptCompiler extends App {
  println(args.toList)
  println(System.getProperty("user.dir"))
  ScriptFactory.fromFile(new File("scripts/ocs-sequencer.ss"))
}
