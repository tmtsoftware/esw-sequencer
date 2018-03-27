import tmt.sequencer.ScriptImports._

init[IrisSequencer3]

class IrisSequencer3(cs: CommandService) extends Script(cs) {

  var resultCount = 0
  var eventCount = 0

  override def onSetup(command: Command): Future[CommandResult] = spawn {
    if (command.name == "setup-iris") {
      val topR = cs.setup3("iris-assembly1", command).await
      Thread.sleep(2000)
      resultCount = resultCount + 1
      val result = if (topR.isInstanceOf[CommandResult.Failed]) {
        Thread.sleep(2000)
        cs.setup3("iris-assembly2", command).await
      } else {
        cs.setup3("iris-assembly3", command).await
      }
      resultCount = resultCount + 1
      val results = CommandResult.Multiple(List(topR, result))
      println(s"final result = $results")
      results
    } else {
      println(s"unknown command=$command")
      CommandResult.Empty
    }
  }

  override def onEvent(event: SequencerEvent): Future[Unit] = spawn {
    eventCount = eventCount + 1
    println(event)
  }

  override def onShutdown(): Future[Unit] = spawn {
    println("shutdown")
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {}

}
