package tmt.approach2

import java.io.File

import tmt.sequencer.Wiring

class Approach2Wiring(scriptFilePath: String) extends Wiring {
  lazy val scriptFactory: ScriptFactory = ScriptFactory.fromFile(new File(scriptFilePath))
  lazy val script: Script = scriptFactory.make(dsl)
  lazy val scriptRunner = new ScriptRunner(script, engine, system)
}
