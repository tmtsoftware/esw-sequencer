package tmt.runner

import tmt.sequencer.ScriptRunner

object Main {
  def main(args: Array[String]): Unit = {
    val sequencerId     = args.headOption.getOrElse("iris")
    val observationMode = args.lastOption.getOrElse("darknight")
    ScriptRunner.run(sequencerId, observationMode, false)
  }
}
