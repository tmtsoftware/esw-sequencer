package tmt.sequencer

object SequencerApp {
  def main(args: Array[String]): Unit = {
    val (sequencerId, observingMode, port, isProd) = args match {
      case Array(sId, oMode, p, isP) => (sId, oMode, Some(p.toInt), isP.toBoolean)
      case Array(sId, oMode, p)      => (sId, oMode, Some(p.toInt), true)
      case Array(sId, oMode)         => (sId, oMode, None, true)
      case _                         => throw new RuntimeException("please provide both sequencerId and observationMode parameters")
    }

    val wiring = new Wiring(sequencerId, observingMode, port, isProd)
    import wiring._
    if (isProd) {
      scriptRepo.cloneRepo()
    }
    engine.start(sequencer, script)
//    rpcServer.start()
    rpcServer2.start()
    remoteRepl.server().start()
  }
}
