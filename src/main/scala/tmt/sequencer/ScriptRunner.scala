package tmt.sequencer

object ScriptRunner {
  def run(sequencerId: String, observingMode: String, isProd: Boolean): Unit = {
    val wiring = new Wiring(sequencerId, observingMode, isProd)
    import wiring._
    if (isProd) {
      scriptRepo.cloneRepo()
    }
    supervisorRef
    remoteRepl.server().start()
  }
}
