package tmt.sequencer

import ammonite.ops.{Path, RelPath}

object ScriptRunner {
  def run(scriptFile: String): Unit = {
    val path: Path = ammonite.ops.pwd / RelPath(scriptFile)

    val wiring = new Wiring(path)
    import wiring._

    supervisorRef

    remoteRepl.server().start()
  }
}
