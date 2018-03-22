import tmt.sequencer.ScriptImports._

import scala.collection.mutable

init[IrisSequencer]

class IrisSequencer(cs: CommandService) extends Script(cs) {

  var results: mutable.Buffer[CommandResult] = mutable.Buffer.empty
  var commandCount = 0

  override def onSetup(command: Command): Unit = {
    if (command.name == "setup-iris") {
      cs.setup("iris-assembly1", command)
      commandCount += 1
      cs.setup("iris-assembly2", command)
      commandCount += 1
    } else {
      println(s"unknown command=$command")
    }
  }

  override def onCommandCompletion(commandResult: CommandResult): Unit = {
    results += commandResult
    commandCount -= 1
    if(commandCount == 0) {
      cs.stepComplete(CommandResult.from(results))
    }
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    println(event)
  }
}
