package tmt.sequencer

object ScriptRunner {
  def run(scriptFile: String, isProd: Boolean): Unit = {
    val wiring = new Wiring(scriptFile, isProd)
    import wiring._
    if (isProd) {
      scriptRepo.cloneRepo()
    }
    supervisorRef
    engine
    remoteRepl.server().start()
  }
}
