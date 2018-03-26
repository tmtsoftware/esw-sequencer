import tmt.sequencer.ScriptImports._

init[IrisSequencer]

class IrisSequencer(cs: CommandService) extends Script(cs) {
  var resultCount = 0
  var eventCount = 0
  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      cs.setup("iris-assembly1", command)
    } else {
      println(s"unknown command=$command")
    }
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {
    resultCount = resultCount + 1
    if(command.name == "setup-iris") {
      if(commandResult.isInstanceOf[CommandResult.Failed]) {
        cs.setup("iris-assembly2", Command(Id("command2"), "setup2", List(1, 2)))
      } else {
        cs.setup("iris-assembly3", Command(Id("command3"), "setup3", List(1, 2)))
      }
    } else {
      cs.stepComplete()
    }
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    eventCount = eventCount + 1
    println(event)
  }
}
