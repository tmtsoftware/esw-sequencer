package tmt.runner

import tmt.sequencer.ScriptRunner

object Main {
  def main(args: Array[String]): Unit = {
    ScriptRunner.run("ocs", "darknight", false)
  }
}
