package tmt.tools

import java.io.File

import tmt.sequencer.ScriptFactory

object ScriptCompiler extends App {
  println(args.toList)
  println(System.getProperty("user.dir"))
  ScriptFactory.fromFile(new File("scripts/simple.ss"))
}
