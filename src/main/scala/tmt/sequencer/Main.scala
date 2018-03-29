package tmt.sequencer

object Main {
  def main(args: Array[String]): Unit = {
    val sequencerId     = args.headOption.getOrElse(throw new RuntimeException("sequencerId is missing"))
    val observationMode = args.lastOption.getOrElse(throw new RuntimeException("observation mode is missing"))
    ScriptRunner.run(sequencerId, observationMode, true)
  }
}
