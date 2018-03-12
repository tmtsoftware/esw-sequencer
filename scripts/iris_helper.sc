import tmt.sequencer.ScriptImports._
import $file.helpers

import scala.collection.mutable

println()

class Iris(cs: CommandService) extends Script(cs) {

  var results: mutable.Buffer[CommandResult] = mutable.Buffer.empty

  override def onSetup(command: Command): CommandResult = {
    if (command.name == "setup-iris") {

      val result1 = cs.setup("iris-assembly1", command)
      results += result1
      println(result1)

      val result2 = cs.setup("iris-assembly2", command)
      results += result2
      println(result2)

      CommandResult.Multiple(List(result1, result2))
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
