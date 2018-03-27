import tmt.sequencer.ScriptImports._

init[IrisSequencer3]

class IrisSequencer3(cs: CommandService) extends Script(cs) {

  var eventCount = 0

  override def onSetup(command: Command): Future[CommandResult] = async {
    if (command.name == "setup-iris") {
      val topR = await(cs.setup3("iris-assembly1", command))
      Thread.sleep(2000)
      val result = if (topR.isInstanceOf[CommandResult.Failed]) {
        Thread.sleep(2000)
        List(await(cs.setup3("iris-assembly2", command)))
      } else {
        await(par(cs.setup3("iris-assembly3", command),
        cs.setup3("iris-assembly4", command)))
      }
      val results = CommandResult.Multiple(topR :: result)
      println(s"final result = $results")
      results
    } else {
      println(s"unknown command=$command")
      CommandResult.Empty
    }
  }

  override def onEvent(event: SequencerEvent): Future[Unit] = async {
    eventCount = eventCount + 1
    println(event)
  }

  override def onShutdown(): Future[Unit] = async {
    println("shutdown")
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {}

}
