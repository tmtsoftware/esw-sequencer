package tmt.runner

import tmt.sequencer.ScriptRunner

object Main {
  def main(args: Array[String]): Unit = {
    val scriptFile = args.headOption.getOrElse("src/test/scala/scripts/ocs_sequencer.sc")
    ScriptRunner.run(scriptFile, isProd = false)
  }
}
