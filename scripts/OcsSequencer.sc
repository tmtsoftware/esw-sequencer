import tmt.sequencer.ScriptImports._
import $file.helpers

import scala.collection.mutable

init[OcsSequencer]

class OcsSequencer(cs: CommandService) extends Script(cs) {

  var results: mutable.Buffer[CommandResult] = mutable.Buffer.empty

  override def onSetup(command: Command): CommandResult = {
    if (command.name == "setup-assembly1") {
      val result = cs.setup("assembly1", command)
      results += result
      Thread.sleep(5000)
      println(result)
      result
    }
    else if (command.name == "setup-assemblies-parallel") {
      val (params1, params2) = cs.split(command.params)
      val _results = par(
        cs.setup("assembly1", Command(Id("command-id-1"), "setup-assembly1", params1)),
        cs.setup("assembly2", Command(Id("command-id-2"), "setup-assembly2",  params2))
      )
      val result = CommandResult.Multiple(_results)
      println(result)
      results += result
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
