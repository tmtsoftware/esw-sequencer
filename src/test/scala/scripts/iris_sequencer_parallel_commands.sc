import tmt.sequencer.ScriptImports._

init[IrisSequencer]

class IrisSequencer(cs: CommandService) extends Script(cs) {
  var resultCount = 0
  var eventCount = 0
  var results = List.empty[CommandResult]

  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      cs.setup("iris-assembly1", command)
    } else {
      println(s"unknown command=$command")
    }
  }

  override def onCommandCompletion(command: Command, commandResult: CommandResult): Unit = {
    resultCount = resultCount + 1
    results = results :+ commandResult
    if(command.name == "setup-iris") {
      par(
        cs.setup("iris-assembly2", Command(Id("command2"), "setup2", List(1, 2))),
        cs.setup("iris-assembly3", Command(Id("command3"), "setup3", List(1, 2)))
      ) //Putting 2 commands in par is not making any difference
    } else if (resultCount == 3){
      cs.stepComplete(results)
      results = List.empty[CommandResult]
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
