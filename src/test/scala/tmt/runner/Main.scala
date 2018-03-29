package tmt.runner

import tmt.sequencer.ScriptRunner

object Main {
  def main(args: Array[String]): Unit = {
    args match {
      case Array(sequencerId, observationMode) => ScriptRunner.run(sequencerId, observationMode, false)
      case _                                   => throw new RuntimeException("please provide both sequencerId and observationMode parameters")
    }
  }
}
