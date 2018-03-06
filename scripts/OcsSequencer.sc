import tmt.sequencer.ScriptImports._
import $file.helpers

init[OcsSequencer]

class OcsSequencer(cs: CommandService) extends Script(cs) {

  override def onSetup(command: Command): Unit = {
    if (command.id == "setup-assembly1") {
      println(cs.setup("assembly1", command))
    }
    else if (command.id == "setup-assembly2") {
      println(cs.setup("assembly2", command))
    }
    else if (command.id == "setup-assemblies-sequential") {
      val (params1, params2) = cs.split(command.params)
      println(cs.setup("assembly1", Command("setup-assembly1", params1)))
      println(cs.setup("assembly2", Command("setup-assembly2", params2)))
    }
    else if (command.id == "setup-assemblies-parallel") {
      val (params1, params2) = cs.split(command.params)
      val responses = par(
        cs.setup("assembly1", Command("setup-assembly1", params1)),
        cs.setup("assembly2", Command("setup-assembly2", params2))
      )
      println(responses)
    }
    else {
      println(s"unknown command=$command")
    }
  }


  override def onObserve(x: Command): Unit = {
    println("observe")
    println(helpers.square(99))
  }

  override def onShutdown(): Unit = {
    println("shutdown")
  }

  override def onEvent(event: SequencerEvent): Unit = {
    println(event)
  }
}
