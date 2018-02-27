import tmt.approach3.Script._

init[OcsSequencer]

class OcsSequencer(cs: CommandService, engine: Engine) extends Script(cs, engine) {

  override def onCommand(command: Command): Unit = {
    if (command.name == "setup-assembly1") {
      println(cs.setup("assembly1", command))
    }
    else if (command.name == "setup-assembly2") {
      println(cs.setup("assembly2", command))
    }
    else if (command.name == "setup-assemblies-sequential") {
      val (params1, params2) = cs.split(command.params)
      println(cs.setup("assembly1", Command("setup-assembly1", params1)))
      println(cs.setup("assembly2", Command("setup-assembly2", params2)))
    }
    else if (command.name == "setup-assemblies-parallel") {
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

  override def onShutdown(): Unit = {
    println("shutdown")
  }

}
