import tmt.sequencer.ScriptImports._

init[IrisSequential]

class IrisSequential(cs: CswServices) extends Script(cs) {

  var resultCount = 0
  var eventCount = 0

  override def onSetup(command: Command): Future[CommandResults] = spawn {
    if (command.name == "setup-iris") {
      val topR = cs.setup("iris-assembly1", command).await
      Thread.sleep(2000)
      resultCount = resultCount + 1
      val result = if (topR.isInstanceOf[CommandResult.Failed]) {
        Thread.sleep(2000)
        cs.setup("iris-assembly2", command).await
      } else {
        cs.setup("iris-assembly3", command).await
      }
      resultCount = resultCount + 1
      val results = CommandResults(List(topR, result))
      println(s"final result = $results")
      results
    } else {
      println(s"unknown command=$command")
      CommandResults.empty
    }
  }

  override def onEvent(event: SequencerEvent): Future[Unit] = spawn {
    eventCount = eventCount + 1
    println(event)
  }

  override def onShutdown(): Future[Unit] = spawn {
    println("shutdown")
  }
}
