import tmt.sequencer.ScriptImports._
import $file.helpers

import scala.collection.mutable

init[OcsSequencer]

class OcsSequencer(cs: CommandService) extends Script(cs) {

  var results: mutable.Buffer[CommandResult] = mutable.Buffer.empty

  override def onSetup(command: Command): CommandResult = {
    if (command.name == "setup-assembly1") {
      println("*" * 50)
      val result = cs.setup("assembly1", command)
      results += result
      Thread.sleep(10000)
      println(result)
      println("")
      println("")
      result
    }
    else {
      println(s"unknown command=$command")
      CommandResult.Empty
    }
  }


  override def onObserve(x: Command): CommandResult = {
    println("observe")
    val x = helpers.square(99)
    println(x)
    CommandResult.Single(x.toString)
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    println(event)
  }
}
