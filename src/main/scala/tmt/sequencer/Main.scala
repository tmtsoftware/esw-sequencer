package tmt.sequencer

object Main {
  def main(args: Array[String]): Unit = {
    val scriptFile = args.headOption.getOrElse(throw new RuntimeException("script name is missing"))
    ScriptRunner.run(scriptFile, isProd = true)
  }
}
