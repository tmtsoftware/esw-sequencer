package tmt.tools

import java.nio.file.{Files, Paths}

import tmt.sequencer.ScriptFactory

object ScriptCompiler extends App {
  println(args.toList)
  println(System.getProperty("user.dir"))
  ScriptFactory.fromFilePath(args(0))
}
