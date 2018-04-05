import tmt.sequencer.ScriptImports._

class IrisDarkNight(cs: CswServices) extends Script(cs) {

  var eventCount = 0
  var commandCount = 0

  handleCommand("setup-iris") { command =>
    spawn {
      println(s"\n[Iris] Command received - ${command.name}")
      val assembly1Result = cs.setup("iris-assembly1", command).await
      val commandFailed = assembly1Result.isInstanceOf[CommandResult.Failed]

      val results = if (commandFailed) {
        List(cs.setup("iris-assembly2", command).await)
      } else {
        par(
          cs.setup("iris-assembly3", command),
          cs.setup("iris-assembly4", command)
        ).await
      }

      val irisResult = CommandResult.Composite(command.id, results)

      val finalResults = irisResult.prepend(assembly1Result)
      println(s"\n[Iris] Result received - ${command.name} with result - $finalResults")
      finalResults
    }
  }
}
