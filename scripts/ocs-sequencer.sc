import tmt.sequencer.Dsl._

forEach { command =>
  if (command.name == "setup-assembly1") {
    println((command, CS.setup("assembly1", command.params)))
  }
  else if (command.name == "setup-assembly2") {
    println((command, CS.setup("assembly2", command.params)))
  }
  else if (command.name == "setup-assemblies-sequential") {
    val (params1, params2) = CS.split(command.params)
    println((params1, CS.setup("assembly1", params1)))
    println((params2, CS.setup("assembly2", params2)))
  }
  else if (command.name == "setup-assemblies-parallel") {
    val (params1, params2) = CS.split(command.params)
    val responses = par(
      CS.setup("assembly1", params1),
      CS.setup("assembly2", params2)
    )
    println((command, responses))
  }
  else {
    println(s"unknown command=$command")
  }
}
