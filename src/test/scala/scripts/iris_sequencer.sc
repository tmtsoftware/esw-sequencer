import tmt.sequencer.ScriptImports._

init[IrisSequencer]

class IrisSequencer(cs: CommandService) extends Script(cs) {

  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      cs.setup("iris-assembly1", command)
      cs.setup("iris-assembly2", command)
    } else {
      println(s"unknown command=$command")
    }
  }

  override def onCommandCompletion(commandResult: CommandResult): Unit = {}

  override def onStepCompletion(commandResult: CommandResult): Unit = {}

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    println(event)
  }
}
